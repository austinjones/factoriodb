package com.factoriodb.graph;

import com.factoriodb.graph.stream.BasicGraphStream;
import com.factoriodb.model.CrafterType;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.annotation.Resource;

/**
 * @author austinjones
 */
public class GraphUtils {
    public static ResourceGraph solveResourceFlow(final RecipeGraph graph, List<GraphSolver.Constraint> constraints) {
        // create equations for each recipe
        ArrayList<Recipe> recipes = new ArrayList<>();
        recipes.addAll(graph.vertexSet());

        ArrayList<RecipeEdge> edges = new ArrayList<>();
        edges.addAll(graph.edgeSet());

        int width = edges.size() + recipes.size();

        List<double[]> matrix = new ArrayList<>();
        List<Double> rhs = new ArrayList<>();

        for (Recipe recipe : recipes) {
            for (String item : recipe.inputItems.keySet()) {
                double[] line = new double[width];

                int r = recipes.indexOf(recipe);
                line[edges.size() + r] = -1.0 * recipe.inputRate(item);

                int edgesFound = 0;
                for(RecipeEdge edge : graph.incomingEdgesOf(recipe)) {
                    if (!item.equals(edge.getItem())) {
                        continue;
                    }

                    int e = edges.indexOf(edge);
                    line[e] = 1.0;

                    edgesFound++;
                }

                if (edgesFound == 0) {
                    continue;
                }

                matrix.add(line);
                rhs.add(0.0);
            }
        }

        // need variables in solution for proportion of output in monosplit situation!
        // create a line per (vertex, item) pair
        // add 1.0 for all edges that consume or produce resource
        // add -1.0 for vertex input/output rate
        for (Recipe recipe : recipes) {
            for (String item : recipe.outputItems.keySet()) {
                double[] line = new double[width];

                int r = recipes.indexOf(recipe);
                line[edges.size() + r] = -1.0 * recipe.outputRate(item);

                int edgesFound = 0;
                for(RecipeEdge edge : graph.outgoingEdgesOf(recipe)) {
                    if (!item.equals(edge.getItem())) {
                        continue;
                    }

                    int e = edges.indexOf(edge);
                    line[e] = 1.0;

                    edgesFound++;
                }

                if (edgesFound == 0) {
                    continue;
                }

                matrix.add(line);
                rhs.add(0.0);
            }
        }

        for (int c = 0; c < constraints.size(); c++) {
            double[] line = new double[width];
            GraphSolver.Constraint constraint = constraints.get(c);

            Recipe r = graph.getRecipe(constraint.recipe);
            if (r == null) {
                throw new NullPointerException("Unknown recipe from constraint " + constraint.recipe);
            }

            String item = constraint.item;
            if (item == null && r.inputItems.size() == 1) {
                item = r.inputItems.keySet().iterator().next();
            }

            if (!r.inputItems.containsKey(item)) {
                throw new NullPointerException("Unknown item from constraint on recipe " + constraint.recipe);
            }

            int recipeIndex = recipes.indexOf(r);
            double rate = constraint.flow / r.inputRate(item);
            line[edges.size() + recipeIndex] = 1.0;

            matrix.add(line);
            rhs.add(rate);
        }

        double[][] matrixArray = matrix.toArray(new double[0][0]);
        double[] rhsArray = new double[matrixArray.length];
        for (int i = 0; i < rhs.size(); i++) {
            rhsArray[i] = rhs.get(i);
        }

        RealMatrix coefficients = new Array2DRowRealMatrix(matrixArray, false);

        DecompositionSolver solver = new QRDecomposition(coefficients).getSolver();
        RealVector constants = new ArrayRealVector(rhsArray, false);
        RealVector solution = solver.solve(constants);
        System.out.println(solution);

        ResourceGraph rg = new BasicGraphStream<>(graph).map(new ResourceGraph(),
            (g, v) -> new ResourceVertex(v, solution.getEntry(edges.size() + recipes.indexOf(v))),
            (g, e) -> {
                ResourceEdge edge = new ResourceEdge();
                g.setEdgeResource(edge, e.getItem(), solution.getEntry(edges.indexOf(e)));
                return edge;
            }
        );

        return rg;
    }


    public static RecipeGraph connectRecipes(RecipeGraph recipeGraph) {
        RecipeGraph copy = (RecipeGraph)recipeGraph.clone();

        for(Recipe r : copy.vertexSet()) {
            Set<String> unsatisfiedInputs = copy.unsatisfiedInputsOf(r);

            for(String item : unsatisfiedInputs) {
                Set<Recipe> recipes = copy.recipesSupplying(item);
                for(Recipe source : recipes) {
//                    Recipe source = recipes.stream().findFirst().get();
                    RecipeEdge edge = copy.addEdge(source, r);
                    copy.setEdgeResource(edge, item);
                }
//                if(recipes.size() == 0) {
//
//                } else if (recipes.size() == 1) {
//                    Recipe source = recipes.stream().findFirst().get();
//                    RecipeEdge edge = copy.addEdge(source, r);
//                    copy.setEdgeResource(edge, item);
//                } else {
//                    // todo: improve this.  maybe pick one?
//                    throw new IllegalStateException("No unique solution to recipe graph for recipe " + r + ", item " + item);
//                }
            }
        }

        return copy;
    }

    public static RecipeGraph insertInputs(RecipeGraph graph) {
        Map<String, Recipe> added = new HashMap<>();

        Set<Recipe> recipes = new HashSet<>(graph.vertexSet());
        for(Recipe r : recipes) {
            Set<String> inputs = r.inputs();
            Set<String> satisfied = graph.incomingEdgesOf(r)
                    .stream()
                    .map(e -> e.getItem())
                    .collect(Collectors.toSet());
            Set<String> unsatisfied = new HashSet<>();
            unsatisfied.addAll(inputs);
            unsatisfied.removeAll(satisfied);

            for(String input : unsatisfied) {
                Recipe inputRecipe = added.get(input);
                if(inputRecipe == null) {
                    inputRecipe = new Recipe();
                    inputRecipe.name = "input-" + input;
                    inputRecipe.crafterType = CrafterType.INPUT;
                    inputRecipe.outputItems.put(input, 1.0);
                    inputRecipe.time = 1;

                    added.put(input, inputRecipe);

                    graph.addVertex(inputRecipe);
                }

                RecipeEdge edge = new RecipeEdge();
                edge.setItem(input);
                graph.addEdge(inputRecipe, r, edge);
            }
        }

        return graph;
    }


    public static RecipeGraph insertOutputs(RecipeGraph graph) {
        Map<String, Recipe> added = new HashMap<>();

        Set<Recipe> recipes = new HashSet<>(graph.vertexSet());
        for(Recipe r : recipes) {
            Set<String> inputs = r.outputs();
            Set<String> satisfied = graph.outgoingEdgesOf(r)
                    .stream()
                    .map(e -> e.getItem())
                    .collect(Collectors.toSet());
            Set<String> unsatisfied = new HashSet<>();
            unsatisfied.addAll(inputs);
            unsatisfied.removeAll(satisfied);

            for(String output : unsatisfied) {
                Recipe outputRecipe = added.get(output);
                if(outputRecipe == null) {
                    outputRecipe = new Recipe();
                    outputRecipe.name = "output-" + output;
                    outputRecipe.crafterType = CrafterType.OUTPUT;
                    outputRecipe.inputItems.put(output, 1.0);
                    outputRecipe.time = 1;

                    added.put(output, outputRecipe);

                    graph.addVertex(outputRecipe);
                }

                RecipeEdge edge = new RecipeEdge();
                edge.setItem(output);
                graph.addEdge(r, outputRecipe, edge);
            }
        }

        return graph;
    }

    public static ResourceGraph convert(RecipeGraph recipes) {
        ResourceGraph graph = new BasicGraphStream<>(recipes).map(new ResourceGraph(),
                (g, v) -> new ResourceVertex(v, 1.0),
                (g, e) -> {
                    String item = e.getItem();
                    Recipe source = recipes.getEdgeSource(e);
                    Recipe target = recipes.getEdgeTarget(e);

                    ResourceEdge edge = new ResourceEdge();
                    double ratio = source.outputRatio(item, target);

                    if(source.crafterType == CrafterType.INPUT) {
                        ratio = 1.0 / target.inputRate(item);
                    } else if(target.crafterType == CrafterType.OUTPUT) {
                        ratio = source.outputRate(item) / 1.0;
                    }

                    g.setEdgeResource(edge, item, ratio);
                    return edge;
                }
        );

//        for (TopologicalOrderIterator<ResourceVertex, ResourceEdge> it
//             = new TopologicalOrderIterator<>(graph); it.hasNext(); ) {
//            ResourceVertex v = it.next();
//
//            double rate = sourceSpeed(graph, v);
//            respeed(graph, v, v.getRate() * rate);
//        }

        return graph;
    }

    public static TransportGraph calculateTransportOptions(ResourceGraph resourceGraph) {
        return new BasicGraphStream<>(resourceGraph).map(new TransportGraph(),
                (g, v) -> new TransportVertex(v).updateSolutions(g),
                (g, e) -> {
                    String item = e.getItem();
                    TransportEdge edge = new TransportEdge();
                    g.setEdgeResource(edge, e.getItem(), resourceGraph.getEdgeWeight(e));
                    edge.updateSolutions(g);
                    return edge;
                }
        );
    }
}

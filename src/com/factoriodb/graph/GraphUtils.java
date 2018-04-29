package com.factoriodb.graph;

import com.factoriodb.graph.stream.BasicGraphStream;
import com.factoriodb.model.CrafterType;

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

    /**
     * Calculates the crafting speed (1.0 being natural speed) given inputs of 'available output / requested input'
     * @param graph a resource graph
     * @param vertex the resource vertex in the graph
     * @return a double crafting speed.
     */
    public static double sourceSpeed(ResourceGraph graph, ResourceVertex vertex) {
        Collection<ResourceEdge> sources = graph.sourcesOf(vertex);

        Map<String, List<ResourceEdge>> items = sources.stream()
                .collect(Collectors.groupingBy((e) -> e.getItem()));

        double min = items.values().stream()
                .map((l) -> l.stream()
                        .mapToDouble((e) -> graph.getEdgeWeight(e))
                        .filter((e) -> e != Double.POSITIVE_INFINITY)
                        .sum())
                .filter((e) -> e != 0)
                .mapToDouble((e) -> e)
                .min().orElse(1.0);

        return min;
    }

    public static double targetSpeed(ResourceGraph graph, ResourceVertex vertex) {
        Collection<ResourceEdge> targets = graph.targetsOf(vertex);

        Map<String, List<ResourceEdge>> items = targets.stream()
                .collect(Collectors.groupingBy((e) -> e.getItem()));

        List<Double> mins = items.values().stream()
                .map((l) -> l.stream()
                        .mapToDouble((e) -> 1.0 / graph.getEdgeWeight(e))
                        .sum())
                .collect(Collectors.toList());

        double min = mins.stream()
                .mapToDouble((e) -> e)
                .filter(e -> e < 1.0)
                .max().orElse(1.0);

        return min;
    }

    public static void respeed(ResourceGraph graph, ResourceVertex v, double rate) {
        double oldRate = v.getRate();
        double rateFactor = rate / oldRate;

        v.setRate(rate);
        for (ResourceEdge e : graph.sourcesOf(v)) {
            double edgeWeight = graph.getEdgeWeight(e);
            graph.setEdgeWeight(e, edgeWeight / rateFactor);
        }

        for (ResourceEdge e : graph.targetsOf(v)) {
            double edgeWeight = graph.getEdgeWeight(e);
            graph.setEdgeWeight(e, edgeWeight * rateFactor);
        }
    }

    public static ResourceGraph solveResourceFlow(final ResourceGraph graph) {
        // this produces vertices with a rate relative to natural craft speed
        // and edges with a value relative to requested input / available input,
        // but scaled by the transitive craft speed

        // REFACTOR AS:

        // start from inputs, (Breadth first traversal), and increase speed as much as possible
        for (TopologicalOrderIterator<ResourceVertex, ResourceEdge> it
             = new TopologicalOrderIterator<>(graph); it.hasNext(); ) {
            ResourceVertex v = it.next();

            double rate = sourceSpeed(graph, v);
            respeed(graph, v, v.getRate() * rate);
        }

        // start from outputs, (Reverse breadth first), and decrease 'overspeed sources' to match slowest input
//        for (TopologicalOrderIterator<ResourceVertex, ResourceEdge> it
//             = new TopologicalOrderIterator<>(new EdgeReversedGraph(graph)); it.hasNext(); ) {
//            ResourceVertex v = it.next();
//
//            double rate = targetSpeed(graph, v);
////            respeed(graph, v, v.getRate() / rate);
//        }

//        ResourceGraph stable = new BasicGraphStream<>(graph).mapUntilStable(() -> new ResourceGraph(),
//                (g, v) -> { v.setRate(speed(g, v)); return v; },
//                (g, e) -> {
//                    ResourceVertex source = g.getEdgeSource(e);
//                    ResourceVertex target = g.getEdgeTarget(e);
//                    ResourceEdge originalEdge = graph.getEdge(source, target);
//                    double originalWeight = graph.getEdgeWeight(originalEdge);
//
//                    ResourceEdge edge = new ResourceEdge();
//
//                    String resource = e.getItem();
//                    double speed = speed(g, g.getEdgeSource(e));
//
//                    double newWeight = speed * originalWeight;
//                    g.setEdgeResource(edge, resource, newWeight);
//
//                    return edge;
//                });

        // now that the relative edges have stabilized,
        // we convert the edges into absolute craft speed
        // this is the true item flow
        return new BasicGraphStream<>(graph).map(new ResourceGraph(),
                (g, v) -> v,
                (g, e) -> {
                    ResourceVertex source = g.getEdgeSource(e);

                    String resource = e.getItem();
                    double newWeight = source.getRecipe().outputRate(resource);
                    g.setEdgeResource(e, resource, newWeight);

                    return e;
                });
//
//        return new BasicGraphStream<>(stable).map(
//                new ResourceGraph<>(),
//                (g, v) -> v,
//                (g, e) -> {
//                    if(g.getEdgeWeight(e) != Double.POSITIVE_INFINITY) {
//                        return e;
//                    } else {
//                        String resource = e.getItem();
//
//                        ResourceEdge edge = new ResourceEdge();
//                        double speed = speed(g, g.getEdgeTarget(e));
//
//                        g.setEdgeResource(edge, resource, speed);
//
//                        return edge;
//                    }
//                }
//        );
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

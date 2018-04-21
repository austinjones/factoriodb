package com.factoriodb.graph;

import com.factoriodb.graph.stream.BasicGraphStream;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author austinjones
 */
public class GraphUtils {

    /**
     * Calculates the crafting speed (1.0 being natural speed) given inputs of 'requested input / available input'
     * @param graph a resource graph
     * @param vertex the resource vertex in the graph
     * @param <T> some vertex type
     * @return a double crafting speed.
     */
    public static <T> double speed(ResourceGraph<T> graph, T vertex) {
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
                .max().orElse(1.0);

        return min;
    }

    public static <T> ResourceGraph<T> solveResourceFlow(ResourceGraph<T> graph) {
//        ResourceGraph<T> stable =
         return new BasicGraphStream<>(graph).mapUntilStable(() -> new ResourceGraph<T>(),
                (g, v) -> v,
                (g, e) -> {
                    T source = g.getEdgeSource(e);
                    T target = g.getEdgeTarget(e);
                    ResourceEdge originalEdge = graph.getEdge(source, target);
                    double originalWeight = graph.getEdgeWeight(originalEdge);

                    ResourceEdge edge = new ResourceEdge();

                    String resource = e.getItem();
                    double speed = speed(g, g.getEdgeSource(e));

                    g.setEdgeResource(edge, resource, speed * originalWeight);

                    return edge;
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

    private static <T> void mapEdgeSources(Set<ResourceEdge> convertedEdges, ResourceGraph<T> original, ResourceGraph<T> output, T workingVertex, double cumulativeRatio) {
        for(ResourceEdge edge : output.sourcesOf(workingVertex)) {
            T source = output.getEdgeSource(edge);
            T target = output.getEdgeTarget(edge);

            ResourceEdge originalEdge = original.getEdge(source, target);

            double newRatio;
            if(convertedEdges.contains(edge)) {
                newRatio = output.getEdgeWeight(edge) + cumulativeRatio * original.getEdgeWeight(originalEdge);

                System.out.println("Edge " + edge + " was already converted with " + output.getEdgeWeight(edge) + " weight.");
                System.out.println("New weight was " + newRatio);
            } else {
                newRatio = cumulativeRatio * original.getEdgeWeight(originalEdge);
            }
            output.setEdgeWeight(edge, newRatio);
            convertedEdges.add(edge);
            mapEdgeSources(convertedEdges, original, output, output.getEdgeSource(edge), newRatio);
        }
    }


    private static <T> void mapEdgeTarget(Set<ResourceEdge> convertedEdges, ResourceGraph<T> original, ResourceGraph<T> output, T workingVertex, double cumulativeRatio) {
        for(ResourceEdge edge : output.targetsOf(workingVertex)) {
            T source = output.getEdgeSource(edge);
            T target = output.getEdgeTarget(edge);

            ResourceEdge originalEdge = original.getEdge(source, target);

            double newRatio;
            if(convertedEdges.contains(edge)) {
                newRatio = output.getEdgeWeight(edge) + cumulativeRatio * original.getEdgeWeight(originalEdge);

                System.out.println("Edge " + edge + " was already converted with " + output.getEdgeWeight(edge) + " weight.");
                System.out.println("New weight was " + newRatio);
            } else {
                newRatio = cumulativeRatio * original.getEdgeWeight(originalEdge);
            }

            output.setEdgeWeight(edge, newRatio);
            convertedEdges.add(edge);
            mapEdgeTarget(convertedEdges, original, output, output.getEdgeTarget(edge), newRatio);
        }
    }

    private static enum GraphDirection {
        ANCESTOR,
        DESCENDANT;
    }

    public static void solve(ResourceGraph<Recipe> input, Recipe rootVertex) {
        ResourceGraph<Recipe> output = new BasicGraphStream<>(input)
            .map(new ResourceGraph<>(),
                    (g, v) -> v,
                    (g, e) -> {
                        ResourceEdge edge = new ResourceEdge();
                        g.setEdgeResource(edge, e.getItem(), Double.NaN);
                        return edge;
                    }
            );

        solveRecurse(input, output, rootVertex, 1.0);
    }

    private static void solveRecurse(ResourceGraph<Recipe> input, ResourceGraph<Recipe> output, Recipe vertex, double currentTime) {
        // ------ if you are making a downstream choice that is ambiguious (multiple inputs of same resource), split evenly
        // all sources get 1x the requested usage, or 1 if ambiguous due to multiple sources
        // if you have multiple outputs of same resource, let the outputs decide

        double factor = Double.NaN;
        for(ResourceEdge e : output.targetsOf(vertex)) {
            if(Double.isNaN(factor)) {
                factor = output.getEdgeWeight(e);
            }
        }

        for(ResourceEdge e : output.sourcesOf(vertex)) {
            Recipe source = output.getEdgeSource(e);
            Recipe target = output.getEdgeTarget(e);
            double originalWeight = input.getEdgeWeight(input.getEdge(source, target));
            double edgeTime = currentTime * (1.0/originalWeight);

            output.setEdgeWeight(e, edgeTime);
            solveRecurse(input, output, source, edgeTime);
        }
    }

    private static void solveNode(ResourceGraph<Recipe> input, ResourceGraph<Recipe> output, Recipe vertex, double currentTime) {
        Map<String, Double> inputRate = input.inputsOf(vertex);
        Map<String, Double> recipeInputRate = vertex.inputItems;

        double inputFactor = Double.NaN;
        for(String item : recipeInputRate.keySet()) {
            Double rir = recipeInputRate.get(item);
            if(Double.isNaN(inputFactor)) {
                Double ir = inputRate.get(item);
                if(ir != null && rir != null) {
                    inputFactor = ir / rir;
                    // this ratio has been pinned.  no need to correct this one
                    continue;
                }
            }
        }

        Map<String, Double> outputRate = input.outputsOf(vertex);
        Map<String, Double> recipeOutputRate = vertex.outputItems;
    }

    private static void correctTime(ResourceGraph<Recipe> output, ResourceEdge correction, ResourceEdge reference, double factor) {

    }

    public static void solveSet(ResourceGraph<Recipe> graph, Recipe rootVertex) {
        Map<String, List<ResourceEdge>> resources = new HashMap<>();
        for(ResourceEdge e : graph.sourcesOf(rootVertex)) {
            List<ResourceEdge> edges = resources.get(e.getItem());
            if(edges == null) {
                edges = new ArrayList<>();
                resources.put(e.getItem(), edges);
            }

            edges.add(e);
        }

        for(List<ResourceEdge> edges : resources.values()) {
            if(edges.size() == 1) {

            }
        }

        // for each ancestral edge accessible from the root node
        //   if the input ratios are well defined:
        //     calculate time for the inputs
        //   if not:
        //     for each conflicting set of ancestral edges:
        //       calculate the set of accessible parent verteces
        //     assert there is a union between all sets
        //

    }

    public static <T> ResourceGraph<T> solveResourceRatio(ResourceGraph<T> input, T rootVertex) {
        // pick a node
        // for all other edges, calculate their crafting ratio relative to the chosen node

//        Map<ResourceEdge, Double> edgeValues = new HashMap<>();
        ResourceGraph<T> output = new BasicGraphStream<>(input)
                .map(new ResourceGraph<>(),
                    (g, v) -> v,
                    (g, e) -> {
                        ResourceEdge edge = new ResourceEdge();
                        g.setEdgeResource(edge, e.getItem(), input.getEdgeWeight(e));
                        return edge;
                    }
                );

        GraphPathVisitor<ResourceGraph<T>, T, ResourceEdge> visitor = new GraphPathVisitor<ResourceGraph<T>, T, ResourceEdge>() {
            @Override
            public void visitVertex(ResourceGraph<T> graph, Stack<T> vertexPath, Stack<ResourceEdge> edgePath, T vertex) {
                System.out.println("Visit vertex:" + vertex);
                System.out.println(vertexPath);
                System.out.println(edgePath);
            }

            @Override
            public void visitEdge(ResourceGraph<T> graph, Stack<T> vertexPath, Stack<ResourceEdge> edgePath, ResourceEdge edge) {
                System.out.println("Visit edge:" + edge);
                System.out.println(vertexPath);
                System.out.println(edgePath);

                double pathWeight = edgePath.stream()
                        .map(e -> graph.getEdgeWeight(e))
                        .reduce(1.0, (a,b)->a*b);
                T source = graph.getEdgeSource(edge);
                T target = graph.getEdgeTarget(edge);
                ResourceEdge outputEdge = output.getEdge(source, target);
                output.setEdgeWeight(outputEdge, pathWeight);
            }
        };
        // TODO: get this working.
//        visitGraph(input, rootVertex, visitor);
        mapEdgeSources(new HashSet<>(), input, output, rootVertex, 1.0);
        mapEdgeTarget(new HashSet<>(), input, output, rootVertex, 1.0);

        return output;

        // for each node:
        //   calculate the sum of ratios per input item
        //   find the maximum
        //   for i in input-items:
        //     let ratio = max-item-ratio/sum(i.ratio)
        //     if ratio != 1:
        //       multiply i.edge and all parent edges by ratio


        // for each node:
        //   calculate the sum of ratios per input item
        //   find the minimum
        //   if ratio != 1:
        //     multiply all children edges of node by max-ratio
    }

    public static interface GraphPathVisitor<G extends Graph<V,E>, V,E> {
        public void visitVertex(G graph, Stack<V> vertexPath, Stack<E> edgePath, V vertex);
        public void visitEdge(G graph, Stack<V> vertexPath, Stack<E> edgePath, E edge);
    }

    public static <G extends Graph<V,E>, V,E> void visitGraph(G graph, V root, GraphPathVisitor<G,V,E> visitor) {
        Stack<V> vertexPath = new Stack<>();
        Stack<E> edgePath = new Stack<>();

        Set<V> visitedVerteces = new HashSet<>();
        Set<E> visitedEdges = new HashSet<>();

        visitGraphRecurse(GraphDirection.ANCESTOR, graph, root, visitor, vertexPath, edgePath, visitedVerteces, visitedEdges);
        visitGraphRecurse(GraphDirection.DESCENDANT, graph, root, visitor, vertexPath, edgePath, visitedVerteces, visitedEdges);
    }

    private static <G extends Graph<V,E>, V, E> void visitGraphRecurse(
            GraphDirection direction, G graph,
            V vertex, GraphPathVisitor<G, V, E> visitor,
            Stack<V> vertexPath, Stack<E> edgePath,
            Set<V> visitedVerteces, Set<E> visitedEdges)
    {
        boolean visitVertex = !visitedVerteces.contains(vertex);
        if(visitVertex) {
            vertexPath.add(vertex);
            visitor.visitVertex(graph, vertexPath, edgePath, vertex);
            visitedVerteces.add(vertex);
        }

        Set<E> edges = graph.edgesOf(vertex);
        for(E edge : edges) {
            if(visitedEdges.contains(edge)) {
                continue;
            }


            V edgeSource = graph.getEdgeSource(edge);
            V edgeTarget = graph.getEdgeTarget(edge);
            V other = vertex.equals(edgeSource) ? edgeTarget : edgeSource;
            boolean visitEdge = (direction == GraphDirection.ANCESTOR && vertex.equals(edgeTarget))
                    || (direction == GraphDirection.DESCENDANT && vertex.equals(edgeSource));

            if(visitEdge) {
                edgePath.add(edge);
                visitor.visitEdge(graph, vertexPath, edgePath, edge);
                visitedEdges.add(edge);

                visitGraphRecurse(direction, graph, other, visitor, vertexPath, edgePath, visitedVerteces, visitedEdges);

                edgePath.pop();
            }

        }

        if(visitVertex) {
            vertexPath.pop();
        }
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
                    inputRecipe.type = "input";
                    inputRecipe.outputItems.put(input, Double.POSITIVE_INFINITY);
                    inputRecipe.time = 0;

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
                    outputRecipe.type = "output";
                    outputRecipe.inputItems.put(output, 0.0);
                    outputRecipe.time = 0;

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

    public static ResourceGraph<Recipe> convert(RecipeGraph recipes) {
        return new BasicGraphStream<>(recipes).map(new ResourceGraph<>(),
                (g, v) -> v,
                (g, e) -> {
                    String item = e.getItem();
                    Recipe source = recipes.getEdgeSource(e);
                    Recipe target = recipes.getEdgeTarget(e);

                    ResourceEdge edge = new ResourceEdge();
                    double ratio = source.outputRatio(item, target);

                    if("input".equals(source.type)) {
                        ratio = 1.0 / target.inputRate(item);
                    } else if("output".equals(target.type)) {
                        ratio = source.outputRate(item) / 1.0;
                    }

                    g.setEdgeResource(edge, item, ratio);
                    return edge;
                }
        );
    }
}

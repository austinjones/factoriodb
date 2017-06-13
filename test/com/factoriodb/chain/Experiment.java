package com.factoriodb.chain;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Resource;

/**
 * @author austinjones
 */
public class Experiment {
    private static class Node<T> {
        private String desc;
        public T value;

        public Node(String desc, T value) {
            this.desc = desc;
            this.value = value;
        }

        public Node() {}

        @Override
        public String toString() {
            return desc + " " + value.toString();
        }
    }

    public static class ResourceGraph<T> extends DefaultDirectedWeightedGraph<T, ResourceEdge> {
        public ResourceGraph() {
            super(ResourceEdge.class);
        }

        /**
         * Assigns a weight to an edge.
         *
         * @param e edge on which to set weight
         * @param weight new weight for edge
         * @see WeightedGraph#setEdgeWeight(Object, double)
         */
        public void setEdgeResource(ResourceEdge e, String item, double weight)
        {
            assert (e instanceof ResourceEdge) : e.getClass();
            e.item = item;
            this.setEdgeWeight(e, weight);
        }

        public void addResourceEdge(T source, T target, String item, double weight) {
            ResourceEdge edge = this.addEdge(source, target);
            setEdgeResource(edge, item, weight);
        }

        public Set<ResourceEdge> targetsOf(T vertex) {
            Set<ResourceEdge> edges = new HashSet<>();
            for(ResourceEdge e : this.edgesOf(vertex)) {
                T source = this.getEdgeSource(e);
                if (source == vertex) {
                    edges.add(e);
                }
            }

            return edges;
        }

        public Collection<ResourceEdge> sourcesOf(T vertex) {
            Set<ResourceEdge> edges = new HashSet<>();
            for(ResourceEdge e : this.edgesOf(vertex)) {
                T target = this.getEdgeTarget(e);
                if (target == vertex) {
                    edges.add(e);
                }
            }

            return edges;
        }
    }

    public static class ResourceEdge extends DefaultWeightedEdge {
        String item;

        public ResourceEdge() {

        }

        @Override
        public int hashCode() {
            return 13 * (item != null ? item.hashCode() : 0) + 7 * Double.hashCode(getWeight())
                    + 5 * (getSource() != null ? getSource().hashCode() : 0)
                    + 3 * (getTarget() != null ? getTarget().hashCode() : 0);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }

            if(!(obj instanceof ResourceEdge)) {
                return false;
            }

            ResourceEdge other = (ResourceEdge)obj;
            if(other.item == null && this.item != null) {
                return false;
            }

            return other.item.equals(this.item) && other.getWeight() == this.getWeight();
        }

        public String toString() {
            return item + " x " + this.getWeight();
        }
    }

    private static class StablizationException extends RuntimeException {
        public StablizationException() {
            super();
        }

        public StablizationException(String message) {
            super(message);
        }

        public StablizationException(String message, Throwable cause) {
            super(message, cause);
        }

        public StablizationException(Throwable cause) {
            super(cause);
        }

        protected StablizationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    /**
     * Warning: relies on correct implementations of Vertex and Edge equals and hashCode methods.
     * @param from
     * @param targetGenerator
     * @param convertVertex
     * @param convertEdge
     * @param <V>
     * @param <E>
     * @param <G>
     * @return
     */
    private <V,E,G extends Graph<V,E>> G mapUntilStable(G from,
                                            Supplier<G> targetGenerator,
                                            VertexConverter<G,V,V> convertVertex,
                                            EdgeConverter<G,E,E> convertEdge) {
        // the behavior of this function is a little scary.
        // but I think it's worth it for the downstream code simplification
        // the Graph.equals() method that is commonly used (AbstractGraph)
        // relies on hashCode() and equals(Object) implementations of vertex/edge classes.
        // if we can't stabilize the output within 10,000 iterations.  we abort with a RuntimeException.
        int iteration = 0;

        Graph<V,E> last = null;
        G current = from;

        System.out.println("Source: " + current);
        while(last == null || !last.equals(current)) {
            G to = targetGenerator.get();

            Map<V,V> vertexMap = new HashMap<>();
            for(V v1 : current.vertexSet()) {
                V v2 = convertVertex.convert(current, v1);
                vertexMap.put(v1, v2);
                to.addVertex(v2);
            }

            for(E e1 : current.edgeSet()) {
                V sourceV2 = vertexMap.get(current.getEdgeSource(e1));
                V targetV2 = vertexMap.get(current.getEdgeTarget(e1));
                E e2 = convertEdge.convert(current, e1);
                boolean added = to.addEdge(sourceV2, targetV2, e2);
                if(!added) {
                    throw new IllegalStateException("Failed to add edge " + e2 + " from " + sourceV2+ " to " + targetV2);
                }
            }

            last = current;
            current = to;
            iteration++;
            System.out.println("Iteration " + iteration + ": " + current);
            if(iteration > 10000) {
                throw new StablizationException("Didn't stablize: " + current);
            }
        }

        return current;
    }

    public interface VertexConverter<G, V1, V2> {
        public V2 convert(G g, V1 v1);
    }

    public interface EdgeConverter<G, E1, E2> {
        public E2 convert(G g, E1 e1);
    }

    private <V1,V2,E1,E2> Graph<V2,E2> map(Graph<V1,E1> from, Graph<V2,E2> to,
                                   Function<V1,V2> convertVertex, Function<E1, E2> convertEdge) {
        Map<V1, V2> vertexMap = new HashMap<>();
        for(V1 v1 : from.vertexSet()) {
            V2 v2 = convertVertex.apply(v1);
            vertexMap.put(v1, v2);
            to.addVertex(v2);
        }

        for(E1 e1 : from.edgeSet()) {
            V2 sourceV2 = vertexMap.get(from.getEdgeSource(e1));
            V2 targetV2 = vertexMap.get(from.getEdgeTarget(e1));
            E2 e2 = convertEdge.apply(e1);
            to.addEdge(sourceV2, targetV2, e2);
        }

        return to;
    }

    @Test
    public void testSimple() {
        ResourceGraph<String> g = new ResourceGraph<>();

        // take the input graph, and produce edges with the ratio of output items
        // for tree graphs (output is terminal node), this will be 1 for each node.
        // for non-tree graphs (like oil), this will be interesting.
        String r1 = "r1";
        String c1 = "c1";
        String o1 = "o1";

        g.addVertex(o1);
        g.addVertex(c1);
        g.addVertex(r1);

        g.addResourceEdge(r1, c1, "bad", 0.75);
        g.addResourceEdge(r1, o1, "good", 0.25);

        g.addResourceEdge(c1, o1, "good", 1);

        ResourceGraph<Node<Double>> g2 = new ResourceGraph<Node<Double>>();
        map(g, g2,
                (v) -> {
                    Map<String, List<ResourceEdge>> items = g.sourcesOf(v).stream()
                            .collect(Collectors.groupingBy((e) -> e.item));

                    OptionalDouble min = items.values().stream()
                            .map((l) -> l.stream()
                                    .mapToDouble((e) -> g.getEdgeWeight(e))
                                    .sum())
                            .mapToDouble((e) -> e)
                            .min();

                    return new Node<Double>(v, min.orElse(1.0));
                },
                (e) -> e
        );

        ResourceGraph<Node<Double>> g3 = new ResourceGraph<>();
        map(g2, g3,
                (v) -> v,
                (e) ->  {
                    ResourceEdge r = new ResourceEdge();
                    Node<Double> source = g2.getEdgeSource(e);
                    double factor = Math.min(source.value, 1);
                    double speed = g.getEdgeWeight(e) * factor;

                    g3.setEdgeResource(r, e.item, speed);
                    return r;
                }
        );

        ResourceGraph<Node<Double>> g4 = new ResourceGraph<>();
        map(g3, g4,
                (v) -> {
                    Map<String, List<ResourceEdge>> items = g3.sourcesOf(v).stream()
                            .collect(Collectors.groupingBy((e) -> e.item));

                    OptionalDouble min = items.values().stream()
                            .map((l) -> l.stream()
                                    .mapToDouble((e) -> g3.getEdgeWeight(e))
                                    .sum())
                            .mapToDouble((e) -> e)
                            .min();

                    return new Node<Double>(v.desc, min.orElse(1.0));
                },
                (e) -> e
        );
//        BreadthFirstIterator<String, ResourceEdge> iter = new BreadthFirstIterator<>(g);
//        while(iter.hasNext()) {
//            String n = iter.next();
//            List<Double> inputs = new ArrayList<>();
//            for(ResourceEdge e : g.edgesOf(n)) {
//                String source = g.getEdgeSource(e);
//                String target = g.getEdgeTarget(e);
//                if (source == n) {
//                    inputs.add(g.getEdgeWeight(e));
//                }
//            }
//
//            double rate = 1;
//            if(!inputs.isEmpty()) {
//                rate = Collections.min(inputs);
//
//                for(ResourceEdge e : g.edgesOf(n)) {
//                    String source = g.getEdgeSource(e);
//                    String target = g.getEdgeTarget(e);
//
//                }
//            }
//            Node<Double> newNode = new Node(n, rate);
//        }

        System.out.println(g);
        System.out.println(g2);
        System.out.println(g3);
        System.out.println(g4);
    }


    private <T> void solve(ResourceGraph<T> graph) {
        boolean edited = true;
        while(edited) {
            edited = false;
            BreadthFirstIterator<T, ResourceEdge> iter = new BreadthFirstIterator<>(graph);
            while(iter.hasNext()) {
                T vertex = iter.next();
                Collection<ResourceEdge> sources = graph.sourcesOf(vertex);
                Collection<ResourceEdge> targets = graph.targetsOf(vertex);

                Map<String, List<ResourceEdge>> items = sources.stream()
                        .collect(Collectors.groupingBy((e) -> e.item));

                double min = items.values().stream()
                        .map((l) -> l.stream()
                                .mapToDouble((e) -> graph.getEdgeWeight(e))
                                .sum())
                        .mapToDouble((e) -> e)
                        .min().orElse(1.0);

                // maybe max of 1?

                for(ResourceEdge target : targets) {
                    double weight = graph.getEdgeWeight(target);
                    if (weight > min) {
                        edited = true;
                        graph.setEdgeWeight(target, min);
                    }
                }
            }
        }
    }


    private <T> double speed(ResourceGraph<T> graph, T vertex) {
        Collection<ResourceEdge> sources = graph.sourcesOf(vertex);

        Map<String, List<ResourceEdge>> items = sources.stream()
                .collect(Collectors.groupingBy((e) -> e.item));

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

    private <T> ResourceGraph<T> solve2(ResourceGraph<T> graph) {
        return mapUntilStable(graph, () -> new ResourceGraph<T>(),
                (g, v) -> v,
                (g, e) -> {
                    T source = g.getEdgeSource(e);
                    T target = g.getEdgeTarget(e);
                    ResourceEdge originalEdge = graph.getEdge(source, target);
                    double originalWeight = graph.getEdgeWeight(originalEdge);

                    ResourceEdge edge = new ResourceEdge();

                    String resource = e.item;
                    double speed = speed(g, g.getEdgeSource(e));

                    g.setEdgeResource(edge, resource, speed * originalWeight);

                    return edge;
                });
    }

    private <T> ResourceGraph<T> solve3(ResourceGraph<T> graph) {
        return mapUntilStable(graph, () -> new ResourceGraph<T>(),
                (g, v) -> v,
                (g, e) -> {
                    T source = g.getEdgeSource(e);
                    T target = g.getEdgeTarget(e);
                    ResourceEdge originalEdge = graph.getEdge(source, target);
                    double originalWeight = graph.getEdgeWeight(originalEdge);

                    ResourceEdge edge = new ResourceEdge();

                    String resource = e.item;
                    double speed = speed(g, g.getEdgeSource(e));

                    g.setEdgeResource(edge, resource, speed * originalWeight);

                    return edge;
                });
    }

    @Test
    public void testReal1() {
        ResourceGraph<String> g = new ResourceGraph<>();

        // take the input graph, and produce edges with the ratio of output items
        // for tree graphs (output is terminal node), this will be 1 for each node.
        // for non-tree graphs (like oil), this will be interesting.
        String i1 = "i1";
        String r1 = "r1";
        String c1 = "c1";
        String o1 = "o1";

        g.addVertex(i1);
        g.addVertex(r1);
        g.addVertex(c1);
        g.addVertex(o1);

        g.addResourceEdge(i1, r1, "oil", 1);
        g.addResourceEdge(r1, c1, "bad", 0.75);
        g.addResourceEdge(r1, o1, "good", 0.25);
        g.addResourceEdge(c1, o1, "good", 1);

        System.out.println(g);

        ResourceGraph<String> solved = solve2(g);
        System.out.println( "solve2: " + solved);

        solve(g);
        System.out.println( "solve1: " + g);
    }



    private static class Recipe {
        public String name;
        public Map<String, Double> inputItems = new HashMap<>();
        public Map<String, Double> outputItems = new HashMap<>();
        public double time = 1;

        public Recipe() {

        }

        public Recipe(String item) {
            this.name = item;
            inputItems.put(item, 1.0);
            outputItems.put(item, 1.0);
            this.time = 0;
        }

        public double inputRatio(String input, Recipe source) {
            if(!inputItems.containsKey(input)) {
                throw new IllegalArgumentException("Unknown input " + input + " for recipe " + name);
            }

            if(!source.outputItems.containsKey(input)) {
                throw new IllegalArgumentException("Unknown input " + input + " for recipe " + name);
            }

            return source.outputRate(input) / this.inputRate(input);
        }

        public double ratio(String input, String output) {
            if(!inputItems.containsKey(input)) {
                throw new IllegalArgumentException("Unknown input " + input + " for recipe " + name);
            }

            if(!outputItems.containsKey(output)) {
                throw new IllegalArgumentException("Unknown input " + output + " for recipe " + name);
            }

            return this.inputRate(input) / this.outputRate(output);
        }

        public double outputRatio(String output, Recipe target) {
            if(!target.inputItems.containsKey(output)) {
                throw new IllegalArgumentException("Unknown input " + output + " for recipe " + name);
            }

            if(!outputItems.containsKey(output)) {
                throw new IllegalArgumentException("Unknown input " + output + " for recipe " + name);
            }

            return this.outputRate(output) / target.inputRate(output);
        }

        public double outputRate(String item) {
            return 1.0 * outputItems.get(item) / time;
        }

        public double inputRate(String item) {
            return 1.0 * inputItems.get(item) / time;
        }

        public double totalOutput() {
            if(outputItems.isEmpty()) {
                return 0;
            }

            return outputItems.values()
                    .stream()
                    .mapToDouble((d) -> d)
                    .sum();
        }
    }

    @Test
    public void testRecipe() {
        Recipe oil = new Recipe();
        oil.name = "oil";
        oil.inputItems.put("water", 1.0);
        oil.inputItems.put("oil", 2.0);

        oil.outputItems.put("light", 5.0);
        oil.outputItems.put("heavy", 2.0);
        oil.outputItems.put("petroleum", 3.0);

        Recipe lightCracking = new Recipe();
        lightCracking.name = "light-cracking";
        lightCracking.inputItems.put("light", 1.25);
        lightCracking.inputItems.put("water", 0.5);

        lightCracking.outputItems.put("petroleum", 2.0);

        Recipe heavyCracking = new Recipe();
        heavyCracking.name = "heavy-cracking";
        heavyCracking.inputItems.put("heavy", 1.0);
        heavyCracking.inputItems.put("water", 0.5);

        heavyCracking.outputItems.put("light", 2.5);

        ResourceGraph<String> g = new ResourceGraph<>();

        String water = "water";
        String i1 = "i1";
        String r1 = "r1";
        String cl = "cL";
        String ch = "cH";
        String o1 = "o1";

        g.addVertex(water);
        g.addVertex(i1);
        g.addVertex(r1);
        g.addVertex(cl);
        g.addVertex(ch);
        g.addVertex(o1);

        g.addResourceEdge(water, r1, "water", Double.POSITIVE_INFINITY);
        g.addResourceEdge(i1, r1, "oil", Double.POSITIVE_INFINITY);

        g.addResourceEdge(r1, cl, "light", oil.outputRatio("light", lightCracking));
        g.addResourceEdge(r1, ch, "heavy", oil.outputRatio("heavy", heavyCracking));
        g.addResourceEdge(r1, o1, "petroleum", oil.outputRate("petroleum"));

        g.addResourceEdge(water, cl, "water", Double.POSITIVE_INFINITY);
        g.addResourceEdge(water, ch, "water", Double.POSITIVE_INFINITY);

        // TODO: how do we output 2 when there is more output than input?
        g.addResourceEdge(ch, cl, "light", heavyCracking.outputRatio("light", lightCracking));
        g.addResourceEdge(cl, o1, "petroleum", lightCracking.outputRate("petroleum"));

        System.out.println(g);
        System.out.println(solve3(g));


    }

    @Test
    public void testReal2() {
        ResourceGraph<String> g = new ResourceGraph<>();

        // take the input graph, and produce edges with the ratio of output items
        // for tree graphs (output is terminal node), this will be 1 for each node.
        // for non-tree graphs (like oil), this will be interesting.
        String water = "water";
        String i1 = "i1";
        String r1 = "r1";
        String cl = "cL";
        String ch = "cH";
        String o1 = "o1";

        g.addVertex(water);
        g.addVertex(i1);
        g.addVertex(r1);
        g.addVertex(cl);
        g.addVertex(ch);
        g.addVertex(o1);

        // let v be a vertex.
        // if there are multiple paths through verticies to any source s from v, than either:
        // - v is perfectly fed, or
        // - v is rate limited by one of it's sources

        // if there are not multiple routes to any source s from v, than:
        // - any resources which are requested from v
        //     can be generated by requesting the appropriate resources from v's sources

        //

        // item output / required input (per craft)
        g.addResourceEdge(water, r1, "water", 1);
        g.addResourceEdge(i1, r1, "oil", 1);
        g.addResourceEdge(r1, cl, "light", 5.0 / 2);
        g.addResourceEdge(r1, ch, "heavy", 2.0 / 2);
        g.addResourceEdge(r1, o1, "pet", 3.0 / 1);

        g.addResourceEdge(water, cl, "water", 1);
        g.addResourceEdge(water, ch, "water", 1);

        // TODO: how do we output 2 when there is more output than input?
        g.addResourceEdge(ch, cl, "light", 1);
        g.addResourceEdge(cl, o1, "pet", 1);

        System.out.println(solve2(g));

        System.out.println(g);
        solve(g);
        System.out.println(g);
    }

//    @Test
//    public void test() {
//        Graph<Node, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
//
//        Node n1 = new Node("n1", 1.0);
//        Node n2 = new Node("n2", 2.0);
//        Node n3 = new Node("n3", 1.0);
//
//        g.addVertex(n3);
//        g.addVertex(n2);
//        g.addVertex(n1);
//
//        g.addEdge(n1, n2);
//        g.addEdge(n2, n3);
//        g.addEdge(n1, n3);
//
//        DefaultDirectedWeightedGraph<Node, DefaultWeightedEdge> g2 = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
//        for(Node n : g.vertexSet()) {
//            g2.addVertex(n);
//        }
//
//        for(Node n : g.vertexSet()) {
//            for(DefaultEdge edge : g.edgesOf(n)) {
//                Node source = g.getEdgeSource(edge);
//                if(source == n) {
//                    Node target = g.getEdgeTarget(edge);
//                    DefaultWeightedEdge e2 = g2.addEdge(source, target);
//                    double val = 1.0 * source.value / target.value;
//                    System.out.println("" + source + " -> " + target + " = " + val);
//                    g2.setEdgeWeight(e2, val);
//                }
//            }
//        }
//
//        BreadthFirstIterator<Node, DefaultEdge> iter = new BreadthFirstIterator<>(g);
//        while(iter.hasNext()) {
//            Node n = iter.next();
//
//            List<Double> values = new ArrayList<>();
//            for(DefaultEdge e : g.edgesOf(n)) {
//                Node source = g.getEdgeSource(e);
//                Node target = g.getEdgeTarget(e);
//                if(source == n) {
//                    values.add(1.0 * target.rate * target.input / source.output);
//                }
//            }
//
//            if(values.size() > 0) {
//                n.rate = Collections.min(values);
//                System.out.println("Set " + n.desc + " rate to " + n.rate);
//            }
//
//
//            System.out.println(n.toString() + " - " +  g.edgesOf(n));
//        }
//
//        System.out.println(g);
//    }
}

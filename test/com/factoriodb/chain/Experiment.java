package com.factoriodb.chain;

import com.factoriodb.graph.GraphSolver;
import com.factoriodb.graph.TransportGraph;
import com.factoriodb.graph.stream.BasicGraphStream;
import com.factoriodb.graph.GraphUtils;
import com.factoriodb.graph.Recipe;
import com.factoriodb.graph.RecipeGraph;
import com.factoriodb.graph.ResourceEdge;
import com.factoriodb.graph.ResourceGraph;

import org.jgrapht.traverse.BreadthFirstIterator;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

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

    @Test
    public void testRecipeAssembly() {
        Recipe finished = new Recipe();
        finished.name = "finished";
        finished.inputItems.put("intermediate", 1.0);
        finished.outputItems.put("finished", 5.0);

        Recipe intermediate = new Recipe();
        intermediate.name = "intermediate";
        intermediate.inputItems.put("plate", 1.0);
        intermediate.outputItems.put("intermediate", 2.0);

        RecipeGraph recipeGraph = new RecipeGraph();
        recipeGraph.addVertex(finished);
        recipeGraph.addVertex(intermediate);

        RecipeGraph connected = GraphUtils.connectRecipes(recipeGraph);
        connected = GraphUtils.insertInputs(connected);
        connected = GraphUtils.insertOutputs(connected);

        Recipe root = connected.vertexSet().stream().filter(r -> r.name.equals("output-finished")).findFirst().get();
        ResourceGraph connectedResources = GraphUtils.convert(connected);
//        ResourceGraph solved = GraphUtils.solveResourceFlow(connectedResources);

        System.out.println("Raw: " + recipeGraph);
        System.out.println("Connected: " + connected);
        System.out.println("Resource ratio: " + connectedResources);
//        System.out.println("Solved: " + solved);
    }

    @Test
    public void testRecipeAssemblyOil() {
        Recipe oil = new Recipe();
        oil.name = "oil";
        oil.inputItems.put("water", 1.0);
        oil.inputItems.put("oil", 1.0);
        oil.outputItems.put("heavy", 1.0);
        oil.outputItems.put("light", 2.0);
        oil.outputItems.put("petroleum", 4.0);

        Recipe heavyCracking = new Recipe();
        heavyCracking.name = "heavy-cracking";
        heavyCracking.inputItems.put("heavy", 1.0);
        heavyCracking.inputItems.put("water", 1.0);
        heavyCracking.outputItems.put("light", 4.0);

        Recipe lightCracking = new Recipe();
        lightCracking.name = "light-cracking";
        lightCracking.inputItems.put("light", 1.0);
        lightCracking.inputItems.put("water", 1.0);
        lightCracking.outputItems.put("petroleum", 2.0);

        RecipeGraph recipeGraph = new RecipeGraph();
        recipeGraph.addVertex(oil);
        recipeGraph.addVertex(lightCracking);
        recipeGraph.addVertex(heavyCracking);

        RecipeGraph connected = GraphUtils.connectRecipes(recipeGraph);
        connected = GraphUtils.insertInputs(connected);
        connected = GraphUtils.insertOutputs(connected);
        ResourceGraph connectedResources = GraphUtils.convert(connected);
//        ResourceGraph solved = GraphUtils.solveResourceFlow(connectedResources);

        Recipe root = connected.vertexSet().stream().filter(r -> r.name.equals("output-petroleum")).findFirst().get();
        Recipe input = connected.vertexSet().stream().filter(r -> r.name.equals("input-oil")).findFirst().get();
        Recipe waterin = connected.vertexSet().stream().filter(r -> r.name.equals("input-water")).findFirst().get();
        System.out.println("Raw: " + recipeGraph);
        System.out.println("Connected: " + connected);
        System.out.println("Resource ratio: " + connectedResources);
//        System.out.println("Solved: " + solved);
    }

//    @Test
//    public void testRecipe() {
//        Recipe oil = new Recipe();
//        oil.name = "oil";
//        oil.inputItems.put("water", 1.0);
//        oil.inputItems.put("oil", 2.0);
//
//        oil.outputItems.put("light", 5.0);
//        oil.outputItems.put("heavy", 2.0);
//        oil.outputItems.put("petroleum", 3.0);
//
//        Recipe lightCracking = new Recipe();
//        lightCracking.name = "light-cracking";
//        lightCracking.inputItems.put("light", 1.25);
//        lightCracking.inputItems.put("water", 0.5);
//
//        lightCracking.outputItems.put("petroleum", 2.0);
//
//        Recipe heavyCracking = new Recipe();
//        heavyCracking.name = "heavy-cracking";
//        heavyCracking.inputItems.put("heavy", 1.0);
//        heavyCracking.inputItems.put("water", 0.5);
//
//        heavyCracking.outputItems.put("light", 2.5);
//
//        ResourceGraph<String> g = new ResourceGraph<>();
//
//        String water = "water";
//        String i1 = "i1";
//        String r1 = "r1";
//        String cl = "cL";
//        String ch = "cH";
//        String o1 = "o1";
//
//        g.addVertex(water);
//        g.addVertex(i1);
//        g.addVertex(r1);
//        g.addVertex(cl);
//        g.addVertex(ch);
//        g.addVertex(o1);
//
//        g.addResourceEdge(water, r1, "water", Double.POSITIVE_INFINITY);
//        g.addResourceEdge(i1, r1, "oil", Double.POSITIVE_INFINITY);
//
//        g.addResourceEdge(r1, cl, "light", oil.outputRatio("light", lightCracking));
//        g.addResourceEdge(r1, ch, "heavy", oil.outputRatio("heavy", heavyCracking));
//        g.addResourceEdge(r1, o1, "petroleum", oil.outputRate("petroleum"));
//
//        g.addResourceEdge(water, cl, "water", Double.POSITIVE_INFINITY);
//        g.addResourceEdge(water, ch, "water", Double.POSITIVE_INFINITY);
//
//        g.addResourceEdge(ch, cl, "light", heavyCracking.outputRatio("light", lightCracking));
//        g.addResourceEdge(cl, o1, "petroleum", lightCracking.outputRate("petroleum"));
//
//        System.out.println(g);
//        System.out.println(GraphUtils.solveResourceFlow(g));
//    }
//
//    @Test
//    public void testFull() {
//        Recipe finished = new Recipe();
//        finished.name = "finished";
//        finished.inputItems.put("intermediate", 1.0);
//        finished.outputItems.put("finished", 2.0);
//
//        Recipe intermediate = new Recipe();
//        intermediate.name = "intermediate";
//        intermediate.inputItems.put("plate", 1.0);
//        intermediate.outputItems.put("intermediate", 4.0);
//
//        Recipe smelting = new Recipe();
//        smelting.name = "plate";
//        smelting.inputItems.put("raw", 1.0);
//        smelting.inputItems.put("fuel", 1.0);
//
//        smelting.outputItems.put("plate", 1.0);
//        GraphSolver solver = new GraphSolver();
//
//        TransportGraph graph = solver.solve(smelting, intermediate, finished);
//        System.out.println(graph);
//    }
//
//    @Test
//    public void testRecipe2() {
//        Recipe finished = new Recipe();
//        finished.name = "finished";
//        finished.inputItems.put("intermediate", 1.0);
//
//        finished.outputItems.put("finished", 5.0);
//
//        Recipe intermediate = new Recipe();
//        intermediate.name = "intermediate";
//        intermediate.inputItems.put("plate", 1.25);
//
//        intermediate.outputItems.put("intermediate", 2.0);
//
//        Recipe smelting = new Recipe();
//        smelting.name = "intermediate";
//        smelting.inputItems.put("raw", 1.25);
//        smelting.inputItems.put("fuel", 0.75);
//
//        smelting.outputItems.put("plate", 2.0);
//
//        ResourceGraph<String> g = new ResourceGraph<>();
//
//        String raw = "raw";
//        String s = "s";
//        String i = "i";
//        String f = "f";
//
//        g.addVertex(raw);
//        g.addVertex(s);
//        g.addVertex(f);
//        g.addVertex(i);
//
//        g.addResourceEdge(raw, s, "raw", Double.POSITIVE_INFINITY);
//        g.addResourceEdge(raw, s, "fuel", Double.POSITIVE_INFINITY);
//
//        g.addResourceEdge(s, i, "plate", smelting.outputRatio("plate", intermediate));
//        g.addResourceEdge(i, f, "intermediate", intermediate.outputRatio("intermediate", finished));
//
//        System.out.println(g);
//        System.out.println(GraphUtils.solveResourceFlow(g));
//
//
//    }
//    @Test
//    public void testReal2() {
//        ResourceGraph<String> g = new ResourceGraph<>();
//
//        // take the input graph, and produce edges with the ratio of output items
//        // for tree graphs (output is terminal node), this will be 1 for each node.
//        // for non-tree graphs (like oil), this will be interesting.
//        String water = "water";
//        String i1 = "i1";
//        String r1 = "r1";
//        String cl = "cL";
//        String ch = "cH";
//        String o1 = "o1";
//
//        g.addVertex(water);
//        g.addVertex(i1);
//        g.addVertex(r1);
//        g.addVertex(cl);
//        g.addVertex(ch);
//        g.addVertex(o1);
//
//        // let v be a vertex.
//        // if there are multiple paths through verticies to any source s from v, than either:
//        // - v is perfectly fed, or
//        // - v is rate limited by one of it's sources
//
//        // if there are not multiple routes to any source s from v, than:
//        // - any resources which are requested from v
//        //     can be generated by requesting the appropriate resources from v's sources
//
//        //
//
//        // item output / required input (per craft)
//        g.addResourceEdge(water, r1, "water", 1);
//        g.addResourceEdge(i1, r1, "oil", 1);
//        g.addResourceEdge(r1, cl, "light", 5.0 / 2);
//        g.addResourceEdge(r1, ch, "heavy", 2.0 / 2);
//        g.addResourceEdge(r1, o1, "pet", 3.0 / 1);
//
//        g.addResourceEdge(water, cl, "water", 1);
//        g.addResourceEdge(water, ch, "water", 1);
//
//        // TODO: how do we output 2 when there is more output than input?
//        g.addResourceEdge(ch, cl, "light", 1);
//        g.addResourceEdge(cl, o1, "pet", 1);
//
//        System.out.println(GraphUtils.solveResourceFlow(g));
//
////        System.out.println(g);
////        solve(g);
////        System.out.println(g);
//    }

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

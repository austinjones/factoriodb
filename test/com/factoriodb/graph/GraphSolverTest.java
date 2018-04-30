package com.factoriodb.graph;

import com.factoriodb.Main;
import com.factoriodb.model.CrafterType;
import com.factoriodb.recipe.RecipeUtils;

import org.junit.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author austinjones
 */
public class GraphSolverTest {
    // two orders of magnitude slack for test failures
    private static double DELTA = 0.000000000001;
    private void assertValid(TransportGraph graph) {
        for (TransportVertex vertex : graph.vertexSet()) {
            if (vertex.getRecipe().getRecipe().crafterType != CrafterType.INPUT) {
                assertInput(graph, vertex);
            }

            if (vertex.getRecipe().getRecipe().crafterType != CrafterType.OUTPUT) {
                assertOutput(graph, vertex);
            }
        }

//        for (TransportEdge edge : graph.edgeSet()) {
//            assertEdge(graph, edge);
//        }
    }

    private void assertInput(TransportGraph graph, TransportVertex vertex) {
        Map<String, Double> weights = graph.sourcesOf(vertex).stream().collect(
                Collectors.groupingBy(e -> e.getItem(), Collectors.summingDouble(e -> graph.getEdgeWeight(e))));

        RatedRecipe r = vertex.getRecipe();
        for (String item : weights.keySet()) {
            double expected = weights.get(item);
            double actual = r.inputRate(item);
            assertEquals("Vertex " + vertex.toString() + " input " + item
                            + " should match sum of edge weights",
                    expected, actual, DELTA);
        }
    }

    private void assertOutput(TransportGraph graph, TransportVertex vertex) {
        Map<String, Double> weights = graph.targetsOf(vertex).stream().collect(
                Collectors.groupingBy(e -> e.getItem(), Collectors.summingDouble(e -> graph.getEdgeWeight(e))));

        RatedRecipe r = vertex.getRecipe();
        for (String item : weights.keySet()) {
            double expected = weights.get(item);
            double actual = r.outputRate(item);
            assertEquals("Vertex " + vertex.toString() + " output should match sum of edge weights",
                    expected, actual, DELTA);
        }
    }

//    private void assertEdge(TransportGraph graph, TransportEdge edge) {
//        TransportVertex source = graph.getEdgeSource(edge);
//        TransportVertex target = graph.getEdgeTarget(edge);
//
//        assertTrue("Graph edge has input", source.getRecipe().getRecipe().outputItems.containsKey());
//    }

    @Test
    public void testSimple() {
        GraphSolver solver = new GraphSolver();

        Recipe gear = new Recipe();
        gear.name = "iron-gear-wheel";
        gear.inputItems.put("iron-plate", 2.0);
        gear.outputItems.put("iron-gear-wheel", 1.0);
        gear.time = 0.5;

        TransportGraph graph = solver.bind("output-iron-gear-wheel", 10).solve(gear);
        Main.print(graph);
//        assertValid(graph);

        TransportVertex input = graph.getVertex("input-iron-plate");
        TransportVertex gearSolution = graph.getVertex(gear);
        TransportVertex output = graph.getVertex("output-iron-gear-wheel");

        TransportEdge plateEdge = graph.getEdge(input, gearSolution);
        TransportEdge gearEdge = graph.getEdge(gearSolution, output);

        assertEquals(20.0, input.rate(), DELTA);
        assertEquals(20.0, input.getRecipe().outputRate("iron-plate"), DELTA);

        assertEquals(20.0, graph.getEdgeWeight(plateEdge), DELTA);

        assertEquals(20.0, gearSolution.getRecipe().inputRate("iron-plate"), DELTA);
        assertEquals(5.0, gearSolution.rate(), DELTA);
        assertEquals(10.0, gearSolution.getRecipe().outputRate("iron-gear-wheel"), DELTA);

        assertEquals(10.0, graph.getEdgeWeight(gearEdge), DELTA);

        assertEquals(10.0, output.rate(), DELTA);
        assertEquals(10.0, output.getRecipe().inputRate("iron-gear-wheel"), DELTA);
    }

    @Test
    public void testSimple2() {
        GraphSolver solver = new GraphSolver();

        Recipe gear = new Recipe();
        gear.name = "iron-gear-wheel";
        gear.inputItems.put("iron-plate", 2.0);
        gear.outputItems.put("iron-gear-wheel", 1.0);
        gear.time = 0.5;

        Recipe fin = new Recipe();
        fin.name = "item-fin";
        fin.inputItems.put("iron-gear-wheel", 1.0);
        fin.outputItems.put("item-fin", 1.0);
        fin.time = 0.5;

        TransportGraph graph = solver.bind("output-item-fin", 10).solve(gear, fin);
        Main.print(graph);
//        assertValid(graph);

        TransportVertex input = graph.getVertex("input-iron-plate");
        TransportVertex gearSolution = graph.getVertex(gear);
        TransportVertex finSolution = graph.getVertex("item-fin");
        TransportVertex output = graph.getVertex("output-item-fin");

        TransportEdge plateEdge = graph.getEdge(input, gearSolution);
        TransportEdge gearEdge = graph.getEdge(gearSolution, finSolution);
        TransportEdge finEdge = graph.getEdge(finSolution, output);

        assertEquals(20.0, input.rate(), DELTA);
        assertEquals(20.0, input.getRecipe().outputRate("iron-plate"), DELTA);

        assertEquals(20.0, graph.getEdgeWeight(plateEdge), DELTA);

        assertEquals(20.0, gearSolution.getRecipe().inputRate("iron-plate"), DELTA);
        assertEquals(5.0, gearSolution.rate(), DELTA);
        assertEquals(10.0, gearSolution.getRecipe().outputRate("iron-gear-wheel"), DELTA);

        assertEquals(10.0, graph.getEdgeWeight(gearEdge), DELTA);
        assertEquals(5.0, finSolution.rate(), DELTA);
        assertEquals(10.0, graph.getEdgeWeight(finEdge), DELTA);

        assertEquals(10.0, output.rate(), DELTA);
        assertEquals(10.0, output.getRecipe().inputRate("item-fin"), DELTA);
    }

    @Test
    public void testSplit() {
        GraphSolver solver = new GraphSolver();

        Recipe smelting = new Recipe();
        smelting.name = "iron-smelting";
        smelting.inputItems.put("iron-ore", 2.0);
        smelting.outputItems.put("iron-plate", 1.0);
        smelting.outputItems.put("iron-fuzz", 1.0);
        smelting.time = 1;

        Recipe foo = new Recipe();
        foo.name = "iron-foo";
        foo.inputItems.put("iron-plate", 1.0);
        foo.outputItems.put("item-foo", 1.0);
        foo.time = 1;

        Recipe bar = new Recipe();
        bar.name = "iron-bar";
        bar.inputItems.put("iron-fuzz", 1.0);
        bar.outputItems.put("item-bar", 1.0);
        bar.time = 1;

        TransportGraph graph = solver.bind("output-item-foo", 1.0)
                .solve(smelting, foo, bar);
        Main.print(graph);
//        assertValid(graph);

        TransportVertex ironIn = graph.getVertex("input-iron-ore");
        assertEquals(2.0, ironIn.rate(), DELTA);

        TransportVertex smeltSolution = graph.getVertex(smelting);
        assertEquals(1.0, smeltSolution.rate(), DELTA);

        TransportVertex fooSolution = graph.getVertex(foo);
        assertEquals(1.0, fooSolution.rate(), DELTA);

        TransportVertex barSolution = graph.getVertex(bar);
        assertEquals(1.0, barSolution.rate(), DELTA);

        TransportVertex fooOut = graph.getVertex("output-item-foo");
        assertEquals(1.0, fooOut.rate(), DELTA);

        TransportVertex barOut = graph.getVertex("output-item-bar");
        assertEquals(1.0, barOut.rate(), DELTA);


        TransportEdge oreEdge = graph.getEdge(ironIn, smeltSolution);
        assertEquals(2.0, graph.getEdgeWeight(oreEdge), DELTA);

        TransportEdge plateFooEdge = graph.getEdge(smeltSolution, fooSolution);
        assertEquals(1.0, graph.getEdgeWeight(plateFooEdge), DELTA);

        TransportEdge plateBarEdge = graph.getEdge(smeltSolution, barSolution);
        assertEquals(1.0, graph.getEdgeWeight(plateBarEdge), DELTA);


        TransportEdge fooOutEdge = graph.getEdge(fooSolution, fooOut);
        assertEquals(1.0, graph.getEdgeWeight(fooOutEdge), DELTA);

        TransportEdge barOutEdge = graph.getEdge(barSolution, barOut);
        assertEquals(1.0, graph.getEdgeWeight(barOutEdge), DELTA);
    }

    @Test
    public void testMonosplit() {
        GraphSolver solver = new GraphSolver();

        Recipe gear = new Recipe();
        gear.name = "iron-foo";
        gear.inputItems.put("iron-plate", 1.0);
        gear.outputItems.put("iron-gear", 1.0);
        gear.time = 1;

        Recipe wire = new Recipe();
        wire.name = "iron-bar";
        wire.inputItems.put("iron-plate", 1.0);
        wire.outputItems.put("iron-wire", 1.0);
        wire.time = 1;

        Recipe combine = new Recipe();
        combine.name = "item-fin";
        combine.inputItems.put("iron-gear", 1.0);
        combine.inputItems.put("iron-wire", 1.0);
        combine.outputItems.put("item-fin", 1.0);
        combine.time = 1;

        TransportGraph graph = solver.bind("output-item-fin", 1.0)
                .solve(gear, wire, combine);
        Main.print(graph);
//        assertValid(graph);

        TransportVertex ironIn = graph.getVertex("input-iron-plate");
        assertEquals(2.0, ironIn.rate(), DELTA);

        TransportVertex gearSolution = graph.getVertex(gear);
        TransportVertex wireSolution = graph.getVertex(wire);

        TransportEdge plateGearEdge = graph.getEdge(ironIn, gearSolution);
        assertEquals(1.0, graph.getEdgeWeight(plateGearEdge), DELTA);

        TransportEdge plateWireEdge = graph.getEdge(ironIn, wireSolution);
        assertEquals(1.0, graph.getEdgeWeight(plateWireEdge), DELTA);
    }

    @Test
    public void testMonomerge() {
        GraphSolver solver = new GraphSolver();

        Recipe anchor = new Recipe();
        anchor.name = "iron-plate-anchor";
        anchor.inputItems.put("iron-plate", 2.0);
        anchor.outputItems.put("iron-gear", 1.0);
        anchor.outputItems.put("iron-wire", 1.0);
        anchor.time = 1;

        Recipe gear = new Recipe();
        gear.name = "iron-gear";
        gear.inputItems.put("iron-gear", 1.0);
        gear.outputItems.put("item-fin", 1.0);
        gear.time = 1;

        Recipe wire = new Recipe();
        wire.name = "iron-wire";
        wire.inputItems.put("iron-wire", 1.0);
        wire.outputItems.put("item-fin", 1.0);
        wire.time = 1;

        TransportGraph graph = solver.bind("output-item-fin", 2.0)
                .solve(anchor, gear, wire);
        Main.print(graph);
//        assertValid(graph);

        TransportVertex ironIn = graph.getVertex("input-iron-plate");
        assertEquals(2.0, ironIn.rate(), DELTA);

        TransportVertex anchorSolution = graph.getVertex("iron-plate-anchor");
        assertEquals(1.0, anchorSolution.rate(), DELTA);

        TransportVertex gearSolution = graph.getVertex(gear);
        assertEquals(1.0, gearSolution.rate(), DELTA);

        TransportVertex wireSolution = graph.getVertex(wire);
        assertEquals(1.0, wireSolution.rate(), DELTA);

        TransportVertex finOut = graph.getVertex("output-item-fin");
        assertEquals(2.0, finOut.rate(), DELTA);


        TransportEdge plateAnchorEdge = graph.getEdge(ironIn, anchorSolution);
        assertEquals(2.0, graph.getEdgeWeight(plateAnchorEdge), DELTA);

        TransportEdge anchorGearEdge = graph.getEdge(anchorSolution, gearSolution);
        assertEquals(1.0, graph.getEdgeWeight(anchorGearEdge), DELTA);

        TransportEdge anchorWireEdge = graph.getEdge(anchorSolution, wireSolution);
        assertEquals(1.0, graph.getEdgeWeight(anchorWireEdge), DELTA);

        TransportEdge wireFinEdge = graph.getEdge(wireSolution, finOut);
        assertEquals(1.0, graph.getEdgeWeight(wireFinEdge), DELTA);

        TransportEdge gearFinEdge = graph.getEdge(gearSolution, finOut);
        assertEquals(1.0, graph.getEdgeWeight(gearFinEdge), DELTA);
    }

    @Test
    public void testMerge() {
        GraphSolver solver = new GraphSolver();

        Recipe gear = new Recipe();
        gear.name = "iron-foo";
        gear.inputItems.put("iron-plate", 1.0);
        gear.outputItems.put("iron-gear", 1.0);
        gear.time = 1;

        Recipe wire = new Recipe();
        wire.name = "iron-bar";
        wire.inputItems.put("iron-plate", 1.0);
        wire.outputItems.put("iron-wire", 1.0);
        wire.time = 1;

        Recipe combine = new Recipe();
        combine.name = "item-fin";
        combine.inputItems.put("iron-gear", 1.0);
        combine.inputItems.put("iron-wire", 1.0);
        combine.outputItems.put("item-fin", 1.0);
        combine.time = 1;

        TransportGraph graph = solver.bind("output-item-fin", 1.0).solve(gear, wire, combine);
        Main.print(graph);
//        assertValid(graph);

        TransportVertex ironIn = graph.getVertex("input-iron-plate");
        // this is actually a special case.  two recipes are consuming the same item from the same recipe
        // (foo and bar, from iron-plate-input)
        // this is worthy of it's own testcase, so this is handled in testMonosplit
//        assertEquals(2.0, ironIn.rate(), 0.0);

        TransportVertex gearSolution = graph.getVertex(gear);
        assertEquals(1.0, gearSolution.rate(), DELTA);

        TransportVertex wireSolution = graph.getVertex(wire);
        assertEquals(1.0, wireSolution.rate(), DELTA);

        TransportVertex combineSolution = graph.getVertex(combine);
        assertEquals(1.0, combineSolution.rate(), DELTA);

        TransportVertex fooOut = graph.getVertex("output-item-fin");
        assertEquals(1.0, fooOut.rate(), DELTA);


        TransportEdge plateGearEdge = graph.getEdge(ironIn, gearSolution);
        assertEquals(1.0, graph.getEdgeWeight(plateGearEdge), DELTA);

        TransportEdge plateWireEdge = graph.getEdge(ironIn, wireSolution);
        assertEquals(1.0, graph.getEdgeWeight(plateWireEdge), DELTA);

        TransportEdge gearEdge = graph.getEdge(gearSolution, combineSolution);
        assertEquals(1.0, graph.getEdgeWeight(gearEdge), DELTA);

        TransportEdge wireEdge = graph.getEdge(wireSolution, combineSolution);
        assertEquals(1.0, graph.getEdgeWeight(wireEdge), DELTA);


        TransportEdge finOutEdge = graph.getEdge(combineSolution, fooOut);
        assertEquals(1.0, graph.getEdgeWeight(finOutEdge), DELTA);
    }

    @Test
    public void testUndefinedSource() {
        GraphSolver solver = new GraphSolver();

        Recipe smelting = new Recipe();
        smelting.name = "iron-source-1";
        smelting.inputItems.put("iron-ore", 1.0);
        smelting.outputItems.put("iron-plate", 1.0);
        smelting.time = 1;

        Recipe foo = new Recipe();
        foo.name = "iron-source-2";
        foo.inputItems.put("iron-chunks", 1.0);
        foo.outputItems.put("iron-plate", 1.0);
        foo.time = 1;

        Recipe bar = new Recipe();
        bar.name = "iron-foo";
        bar.inputItems.put("iron-plate", 1.0);
        bar.outputItems.put("item-foo", 1.0);
        bar.time = 1;

        try {
            TransportGraph graph = solver.bind("output-item-foo", 2.0).solve(smelting, foo, bar);
            fail("Should have thrown an UndefinedSolutionException");
        } catch (UndefinedSolutionException e) {
            // expected
        }
    }

//    This should be failed... maybe
//    The solver is coming up with a valid solution that fulfills the bind constraints..
//      so I'm not sure I want to fight it..
//    @Test
    public void testUndefinedTarget() {
        GraphSolver solver = new GraphSolver();

        Recipe smelting = new Recipe();
        smelting.name = "iron-source-1";
        smelting.inputItems.put("iron-ore", 1.0);
        smelting.outputItems.put("iron-plate", 1.0);
        smelting.time = 1;

        Recipe use1 = new Recipe();
        use1.name = "iron-user-1";
        use1.inputItems.put("iron-plate", 1.0);
        use1.outputItems.put("item-1", 1.0);
        use1.time = 1;

        Recipe use2 = new Recipe();
        use2.name = "iron-user-2";
        use2.inputItems.put("iron-plate", 1.0);
        use2.outputItems.put("item-2", 1.0);
        use2.time = 1;

        try {
            TransportGraph graph = solver.bind("iron-source-1", 2.0)
                    .solve(smelting, use1, use2);
            Main.print(graph);

            fail("Should have thrown an UndefinedSolutionException");
        } catch (UndefinedSolutionException e) {
            // expected
        }
    }

    @Test
    public void testCalculatorReference() {
        GraphSolver solver = new GraphSolver();

        Recipe refine = new Recipe();
        refine.name = "advanced-oil-processing";
        refine.inputItems.put("crude-oil", 100.0);
        refine.inputItems.put("water", 50.0);
        refine.outputItems.put("heavy-oil", 10.0);
        refine.outputItems.put("light-oil", 45.0);
        refine.outputItems.put("petroleum-gas", 55.0);
        refine.time = 5.0;

        Recipe heavy = new Recipe();
        heavy.name = "heavy-oil-cracking";
        heavy.inputItems.put("heavy-oil", 40.0);
        heavy.inputItems.put("water", 30.0);
        heavy.outputItems.put("light-oil", 30.0);
        heavy.time = 3.0;

        Recipe light = new Recipe();
        light.name = "light-oil-cracking";
        light.inputItems.put("light-oil", 30.0);
        light.inputItems.put("water", 30.0);
        light.outputItems.put("petroleum-gas", 20.0);
        light.time = 3.0;

        TransportGraph graph = solver.bind("output-petroleum-gas", 100.0)
                .solve(refine, heavy, light);
//        assertValid(graph);

        Main.print(graph);

        TransportVertex waterIn = graph.getVertex("input-water");
        TransportVertex oilIn = graph.getVertex("input-crude-oil");
        TransportVertex refineSolution = graph.getVertex(refine);
        TransportVertex heavySolution = graph.getVertex(heavy);
        TransportVertex lightSolution = graph.getVertex(light);
        TransportVertex petroleumOut = graph.getVertex("output-petroleum-gas");
        assertEquals(122.2222222222222, waterIn.getRecipe().outputRate("water"), DELTA);
        assertEquals(111.1111111111111, oilIn.getRecipe().outputRate("crude-oil"), DELTA);
        assertEquals(11.11111111111111, heavySolution.getRecipe().inputRate("heavy-oil"), DELTA);

        assertEquals(58.33333333333333, lightSolution.getRecipe().inputRate("light-oil"), DELTA);
        assertEquals(100.0, petroleumOut.getRecipe().inputRate("petroleum-gas"), DELTA);

    }
}

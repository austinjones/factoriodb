package com.factoriodb.graph;

import org.apache.commons.math3.linear.SingularMatrixException;

import java.util.ArrayList;
import java.util.List;
import com.factoriodb.recipe.Recipe;

/**
 * @author austinjones
 */
public class GraphSolver {
    public static class Constraint {
        String recipe;
        String item;
        double flow;
    }

    private List<Constraint> constraints = new ArrayList<>();
    public GraphSolver() {

    }

    public GraphSolver bind(String recipe, double flow) {
        return bind(recipe, null, flow);
    }

    public GraphSolver bind(String recipe, String item, double flow) {
        Constraint c = new Constraint();
        c.recipe = recipe;
        c.item = item;
        c.flow = flow;

        constraints.add(c);
        return this;
    }

    public TransportGraph solve(Recipe... recipes) {
        RecipeGraph recipeGraph = new RecipeGraph();
        for (Recipe r : recipes) {
            recipeGraph.addVertex(r);
        }

        RecipeGraph connected = GraphUtils.connectRecipes(recipeGraph);
        connected = GraphUtils.insertInputs(connected);
        connected = GraphUtils.insertOutputs(connected);

        try {
            ResourceGraph flows = GraphUtils.solveResourceFlow(connected, constraints);
            return GraphUtils.calculateTransportOptions(flows);
        } catch( SingularMatrixException e) {
            throw new UndefinedSolutionException("Failed to calculate resource flow", e);
        }
    }

//    public void scaleToFlow(ResourceGraph graph, ResourceVertex vertex, double flow) {
//        double inflow = graph.sourcesOf(vertex).stream()
//                .mapToDouble((e) -> graph.getEdgeWeight(e))
//                .sum();
//
//        double scale = flow / inflow;
//        graph.rescale(scale);
//    }

}
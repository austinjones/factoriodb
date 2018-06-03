package org.factoriodb.graph;

import org.apache.commons.math3.linear.SingularMatrixException;
import org.factoriodb.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * @author austinjones
 */
public class GraphSolver {
    public enum ConstraintType {
        INPUT, OUTPUT
    }

    public static class Constraint {
        ConstraintType type = ConstraintType.INPUT;
        String recipe;
        String item;
        double flow;
    }

    private List<Constraint> constraints = new ArrayList<>();
    public GraphSolver() {

    }

    public GraphSolver bind(String recipe, double flow) {
        return bind(recipe, null, flow, ConstraintType.INPUT);
    }

    public GraphSolver bind(String recipe, String item, double flow) {
        return bind(recipe, item, flow, ConstraintType.INPUT);
    }

    public GraphSolver bind(String recipe, String item, double flow, ConstraintType type) {
        Constraint c = new Constraint();
        c.recipe = recipe;
        c.item = item;
        c.flow = flow;
        c.type = type;

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

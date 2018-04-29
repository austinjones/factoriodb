package com.factoriodb.graph;

/**
 * @author austinjones
 */
public class GraphSolver {
    public GraphSolver() {

    }

    public TransportGraph solve(Recipe... recipes) {
        return solve(null, 0.0, recipes);
    }

    public TransportGraph solve(String recipe, double flow, Recipe... recipes) {
        ResourceGraph ratios = calculateRatio(recipes);
        ResourceGraph flows = calculateFlow(ratios);

        if (recipe != null) {
            for (ResourceVertex v : flows.vertexSet()) {
                if (recipe.equals(v.getRecipe().getRecipe().name)) {
                    scaleToFlow(flows, v, flow);
                }
            }
        }

        return GraphUtils.calculateTransportOptions(flows);
    }

    public ResourceGraph calculateRatio(Recipe... recipes) {
        RecipeGraph recipeGraph = new RecipeGraph();
        for (Recipe r : recipes) {
            recipeGraph.addVertex(r);
        }

        RecipeGraph connected = GraphUtils.connectRecipes(recipeGraph);
        connected = GraphUtils.insertInputs(connected);
        connected = GraphUtils.insertOutputs(connected);
        return GraphUtils.convert(connected);
    }

    public ResourceGraph calculateFlow(ResourceGraph graph) {
        return GraphUtils.solveResourceFlow(graph);
    }

    public void scaleToFlow(ResourceGraph graph, ResourceVertex vertex, double flow) {
        double inflow = graph.sourcesOf(vertex).stream()
                .mapToDouble((e) -> graph.getEdgeWeight(e))
                .sum();

        double scale = flow / inflow;
        graph.rescale(scale);
    }

}

package com.factoriodb.graph;

/**
 * @author austinjones
 */
public class GraphSolver {
    public GraphSolver() {

    }

    public ResourceGraph<Recipe> calculateRatio(Recipe... recipes) {
        RecipeGraph recipeGraph = new RecipeGraph();
        for (Recipe r : recipes) {
            recipeGraph.addVertex(r);
        }

        RecipeGraph connected = GraphUtils.connectRecipes(recipeGraph);
        connected = GraphUtils.insertInputs(connected);
        connected = GraphUtils.insertOutputs(connected);
        return GraphUtils.convert(connected);
    }

    public <T> ResourceGraph<T> calculateFlow(ResourceGraph<T> graph) {
        return GraphUtils.solveResourceFlow(graph);
    }

    public <T> void scaleToFlow(ResourceGraph<T> graph, T vertex, double flow) {
        double inflow = graph.sourcesOf(vertex).stream()
                .mapToDouble((e) -> graph.getEdgeWeight(e))
                .sum();

        double scale = flow / inflow;
        graph.rescale(scale);
    }

}

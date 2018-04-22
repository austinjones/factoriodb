package com.factoriodb.graph;

/**
 * @author austinjones
 */
public class TransportVertex {
    private Recipe recipe;

    public TransportVertex(Recipe vertex) {
        this.recipe = vertex;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public List<VertexSolution> calculateSolutions() {

    }
}

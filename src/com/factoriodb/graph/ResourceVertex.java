package com.factoriodb.graph;

/**
 * @author austinjones
 */
public class ResourceVertex {
    private RatedRecipe recipe;
    public ResourceVertex(Recipe recipe, double rate) {
        this.recipe = new RatedRecipe(recipe, rate);
    }

    public RatedRecipe getRecipe() {
        return recipe;
    }

    @Override
    public String toString() {
        return recipe.toString();
    }

    public void setRate(double newRate) {
        this.recipe.setRate(newRate);
    }

    public double getRate() {
        return this.recipe.getRate();
    }
}

package com.factoriodb.graph;

/**
 * @author austinjones
 */
public class RatedRecipe {
    private Recipe recipe;
    private double rate;

    public RatedRecipe(Recipe r, double rate) {
        this.recipe = r;
        this.rate = rate;
    }

    public RatedRecipe withRate(double rate) {
        return new RatedRecipe(recipe, this.rate * rate);
    }

    protected void setRate(double rate) {
        this.rate = rate;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public double getRate() {
        return rate;
    }

    public double inputRate(String item) {
        return rate * this.recipe.inputRate(item);
    }

    public double outputRate(String item) {
        return rate * this.recipe.outputRate(item);
    }

    @Override
    public String toString() {
        return recipe.toString() + " @ " + rate;
    }
}

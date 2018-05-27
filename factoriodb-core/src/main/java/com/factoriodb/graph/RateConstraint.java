package com.factoriodb.graph;

/**
 * @author austinjones
 */
public class RateConstraint {
    private String recipe;
    private double rate;

    public RateConstraint(String recipe, double rate) {
        this.recipe = recipe;
        this.rate = rate;
    }

    public String getRecipe() {
        return recipe;
    }

    public double getRate() {
        return rate;
    }
}

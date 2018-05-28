package org.factoriodb.graph;

import org.factoriodb.recipe.RatedRecipe;
import org.factoriodb.recipe.Recipe;

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

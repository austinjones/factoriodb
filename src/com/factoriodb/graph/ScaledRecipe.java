package com.factoriodb.graph;

import java.util.HashMap;

/**
 * @author austinjones
 */
public class ScaledRecipe extends Recipe {
    public double scale = 1.0;

    public ScaledRecipe(Recipe recipe) {
        this.name = recipe.name;
        this.inputItems = new HashMap<>(recipe.inputItems);
        this.outputItems = new HashMap<>(recipe.outputItems);
        this.time = recipe.time;
        this.type = recipe.type;
    }
}

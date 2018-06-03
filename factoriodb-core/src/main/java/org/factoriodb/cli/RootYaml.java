package org.factoriodb.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * @author austinjones
 */
public class RootYaml {
    private List<RecipeYaml> recipes = new ArrayList<>();

    public List<RecipeYaml> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<RecipeYaml> recipes) {
        this.recipes = recipes;
    }
}

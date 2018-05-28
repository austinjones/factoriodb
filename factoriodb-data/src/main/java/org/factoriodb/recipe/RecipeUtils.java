package org.factoriodb.recipe;

import org.factoriodb.model.CrafterType;
import org.factoriodb.model.Model;

import java.util.List;

public class RecipeUtils {
	private Model m;
	public RecipeUtils(Model m) {
		this.m = m;
	}

    public Recipe recipe(String item) {
        List<Recipe> rs = m.getRecipeByResult(item);
        if(rs == null || rs.isEmpty()) {
            throw new NullPointerException("Unknown recipe for name " + item);
        }

        if(rs.size() > 1) {
            throw new IllegalArgumentException("Too many recipes for name " + item);
        }

        return rs.get(0);
    }

    public Recipe namedRecipe(String recipe) {
        Recipe r = m.getRecipes().get(recipe);
        if(r == null) {
            throw new NullPointerException("Unknown recipe " + recipe);
        }

        return r;
    }

    public Recipe output(String item, double rate) {
	    Recipe r = new Recipe();
	    r.crafterType = CrafterType.OUTPUT;
	    r.name = "output-item";
	    r.time = 1;
	    r.outputItems.put(item, rate);

	    return r;
    }

    public Recipe input(String item, double rate) {
        Recipe r = new Recipe();
        r.crafterType = CrafterType.INPUT;
        r.name = "input-item";
        r.time = 1;
        r.outputItems.put(item, rate);

        return r;
    }
}

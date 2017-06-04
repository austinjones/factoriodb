package com.factoriodb.recipe;

import java.util.List;

import com.factoriodb.chain.Crafter;
import com.factoriodb.chain.Pipe;
import com.factoriodb.model.Item;
import com.factoriodb.model.Model;
import com.factoriodb.chain.assembler.Assembler;
import com.factoriodb.chain.Belt;
import com.factoriodb.model.Recipe;

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

    public Item item(String item) {
        Item i = m.getItemByName(item);
        if(i == null) {
            throw new NullPointerException("Unknown name " + item);
        }

        return i;
    }

	public Crafter craftItem(String item) {
        return new Crafter(recipe(item));
    }

    public Crafter craftRecipe(String recipe) {
        return new Crafter(m.getRecipes().get(recipe));
    }

	public Belt belt(String itemName) {
		Item item = m.getItemByName(itemName);
		if(item == null) {
			throw new NullPointerException("Unknown name " + itemName);
		}
		return new Belt(item);
	}

    public Pipe pipe(String fluidName) {
        Item item = m.getItemByName(fluidName);
        if(item == null) {
            throw new NullPointerException("Unknown name " + fluidName);
        }
        return new Pipe(item);
    }


//	public SplitBelt splitbelt(String left, String right) {
//		Item l = m.getItemByName(left);
//		if(l == null) {
//			throw new NullPointerException("Unknown name " + left);
//		}
//		
//		Item r = m.getItemByName(right);
//		if(r == null) {
//			throw new NullPointerException("Unknown name " + right);
//		}
//		
//		return new SplitBelt(l, r);
//	}
}

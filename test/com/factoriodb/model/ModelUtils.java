package com.factoriodb.model;

import com.factoriodb.com.factoriodb.input.InputFluid;
import com.factoriodb.com.factoriodb.input.InputItem;
import com.factoriodb.com.factoriodb.input.InputModule;
import com.factoriodb.com.factoriodb.input.InputReader;
import com.factoriodb.com.factoriodb.input.InputRecipe;
import com.factoriodb.com.factoriodb.input.InputRecipeItem;

/**
 * @author austinjones
 */
public class ModelUtils {
    public static Model getTestModel() {
        InputItem[] items = new InputItem[] {
                new InputItem("test-ore", 100),
                new InputItem("test-plate", 100),
                new InputItem("test-intermediate", 100),
                new InputItem("test-science", 100)
        };

        InputRecipe smelting = new InputRecipe();
        smelting.name = "test-ore";
        smelting.ingredients.add(new InputRecipeItem("test-ore", 1));
        smelting.result = "test-plate";
        smelting.result_count = 2;
        smelting.energy_required = 0.5;

        InputRecipe intermediate = new InputRecipe();
        intermediate.name = "test-plate";
        intermediate.ingredients.add(new InputRecipeItem("test-plate", 10));
        intermediate.result = "test-intermediate";
        intermediate.result_count = 1;
        intermediate.energy_required = 2;

        InputRecipe science = new InputRecipe();
        science.name = "test-science";
        science.ingredients.add(new InputRecipeItem("test-plate", 10));
        science.ingredients.add(new InputRecipeItem("test-intermediate", 1));
        science.result = "test-science";
        science.result_count = 1;
        science.energy_required = 50;

        InputRecipe[] recipes = new InputRecipe[] {
                smelting,
                intermediate,
                science
        };

        InputFluid spaceFluid = new InputFluid();
        spaceFluid.name = "space-fluid";

        InputFluid[] fluids = new InputFluid[] {spaceFluid};

        Items itemsObj = InputReader.renderItems(items,fluids);
        Recipes recipesObj = InputReader.renderRecipes(itemsObj, recipes);

        return new Model(itemsObj, recipesObj);
    }
}

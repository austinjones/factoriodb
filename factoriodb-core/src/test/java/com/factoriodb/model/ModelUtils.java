package com.factoriodb.model;

import com.factoriodb.input.InputFluid;
import com.factoriodb.input.InputItem;
import com.factoriodb.input.InputReader;
import com.factoriodb.input.InputRecipe;
import com.factoriodb.input.InputRecipeItem;

import java.util.ArrayList;

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

        InputFluid rawFluid = new InputFluid();
        rawFluid.name = "raw-fluid";

        InputFluid spaceFluid = new InputFluid();
        spaceFluid.name = "space-fluid";

        InputFluid badFluid = new InputFluid();
        badFluid.name = "bad-fluid";

        InputFluid okFluid = new InputFluid();
        okFluid.name = "ok-fluid";

        InputFluid[] fluids = new InputFluid[] {rawFluid, spaceFluid, badFluid, okFluid};

        InputRecipe refinery = new InputRecipe();
        refinery.name = "fluid-processing";
        refinery.category = "oil-processing";
        refinery.ingredients.add(new InputRecipeItem("raw-fluid", 1));

        refinery.results = new ArrayList<>();
        InputRecipeItem out1 = new InputRecipeItem();
        out1.amount = 1;
        out1.name = "space-fluid";
        refinery.results.add(out1);

        InputRecipeItem out2 = new InputRecipeItem();
        out2.amount = 2;
        out2.name = "bad-fluid";
        refinery.results.add(out2);

        refinery.result_count = 1;
        refinery.energy_required = 2;

        InputRecipe cracking = new InputRecipe();
        cracking.name = "bad-fluid-cracking";
        cracking.category = "chemistry";
        cracking.ingredients.add(new InputRecipeItem("bad-fluid", 1));

        cracking.result = "ok-fluid";
        cracking.result_count = 1;
        cracking.energy_required = 2;

        InputRecipe cracking2 = new InputRecipe();
        cracking2.name = "ok-fluid-cracking";
        cracking2.category = "chemistry";
        cracking2.ingredients.add(new InputRecipeItem("ok-fluid", 1));

        cracking2.result = "space-fluid";
        cracking2.result_count = 1;
        cracking2.energy_required = 2;

        InputRecipe[] recipes = new InputRecipe[] {
                smelting,
                intermediate,
                science,
                refinery,
                cracking
        };

        Items itemsObj = InputReader.renderItems(items,fluids);
        Recipes recipesObj = InputReader.renderRecipes(itemsObj, recipes);

        return new Model(itemsObj, recipesObj);
    }
}

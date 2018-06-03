package org.factoriodb.chain.option;

import org.factoriodb.recipe.RatedRecipe;

/**
 * @author austinjones
 */
public class IdentityRecipeOption extends RecipeOption {
    public IdentityRecipeOption(RatedRecipe recipe, String optionDescription) {
        super(recipe, optionDescription);
    }

    @Override
    public int count() {
        return 1;
    }

    @Override
    public double input(String item) {
        return ratedRecipe.inputRate(item);
    }

    @Override
    public double output(String item) {
        return ratedRecipe.outputRate(item);
    }

    @Override
    public double maxInput(String item) {
        return ratedRecipe.inputRate(item);
    }

    @Override
    public double maxOutput(String item) {
        return ratedRecipe.outputRate(item);
    }
}

package org.factoriodb.chain.assembler;

import org.factoriodb.chain.option.IdentityRecipeOption;
import org.factoriodb.chain.option.RecipeOption;
import org.factoriodb.recipe.RatedRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author austinjones
 */
public class IdentityAssembler implements Assembler {
    @Override
    public Collection<? extends RecipeOption> options(RatedRecipe recipe) {
        List<IdentityRecipeOption> options = new ArrayList<>();
        options.add(new IdentityRecipeOption(recipe, "identity"));
        return options;
    }
}

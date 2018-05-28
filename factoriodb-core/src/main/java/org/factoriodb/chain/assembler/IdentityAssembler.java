package org.factoriodb.chain.assembler;

import org.factoriodb.chain.option.RecipeOption;
import org.factoriodb.recipe.RatedRecipe;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author austinjones
 */
public class IdentityAssembler implements Assembler {
    @Override
    public Collection<? extends RecipeOption> options(RatedRecipe recipe) {
        return new ArrayList<>();
    }
}

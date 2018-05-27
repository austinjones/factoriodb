package com.factoriodb.chain.assembler;

import com.factoriodb.chain.option.RecipeOption;
import com.factoriodb.graph.RatedRecipe;

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

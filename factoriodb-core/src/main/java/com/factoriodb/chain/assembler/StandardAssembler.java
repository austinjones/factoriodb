package com.factoriodb.chain.assembler;

import com.factoriodb.chain.option.AssemblerOption;
import com.factoriodb.chain.option.RecipeOption;
import com.factoriodb.graph.RatedRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author austinjones
 */
public class StandardAssembler implements Assembler {
    @Override
    public Collection<? extends RecipeOption> options(RatedRecipe recipe) {
        List<AssemblerOption> options = new ArrayList<>();
        options.add(new AssemblerOption(recipe, "assembling-machine-1", 0.5));
        options.add(new AssemblerOption(recipe, "assembling-machine-2", 0.75));
        options.add(new AssemblerOption(recipe, "assembling-machine-3", 1.25));
        return options;
    }
}

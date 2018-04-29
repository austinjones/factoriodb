package com.factoriodb.chain.assembler;

import com.factoriodb.chain.option.AssemblerOption;
import com.factoriodb.chain.option.RecipeOption;
import com.factoriodb.graph.RatedRecipe;
import com.factoriodb.graph.Recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author austinjones
 */
public class SmelterAssembler implements Assembler {
    @Override
    public Collection<? extends RecipeOption> options(RatedRecipe recipe) {
        List<AssemblerOption> options = new ArrayList<>();
        options.add(new AssemblerOption(recipe, "stone-furnace", 1));
        options.add(new AssemblerOption(recipe, "steel-furnace", 2));
        options.add(new AssemblerOption(recipe, "electric-furnace", 2));
        return options;
    }
}

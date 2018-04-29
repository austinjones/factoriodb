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
public class OilRefineryAssembler implements Assembler {
    @Override
    public Collection<? extends RecipeOption> options(RatedRecipe recipe) {
        List<AssemblerOption> options = new ArrayList<>();
        options.add(new AssemblerOption(recipe, "oil-refinery", 1));
        return options;
    }
}

package org.factoriodb.chain.assembler;

import org.factoriodb.chain.option.AssemblerOption;
import org.factoriodb.chain.option.RecipeOption;
import org.factoriodb.recipe.RatedRecipe;

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

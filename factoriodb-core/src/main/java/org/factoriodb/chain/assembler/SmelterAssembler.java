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

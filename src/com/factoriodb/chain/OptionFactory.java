package com.factoriodb.chain;

import com.factoriodb.chain.assembler.Assembler;
import com.factoriodb.chain.option.ConnectionOption;
import com.factoriodb.chain.option.RecipeOption;
import com.factoriodb.graph.RatedRecipe;
import com.factoriodb.graph.Recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author austinjones
 */
public class OptionFactory {
    public static Collection<? extends RecipeOption> recipeOptions(RatedRecipe r) {
        return Assembler.getInstance(r.getRecipe()).options(r);
    }

    public static Collection<? extends ConnectionOption> connectionOptions(String item, double rate) {
        Collection<ConnectionOption> options = new ArrayList<>();
        options.addAll(new Belt(item).options(rate));
        options.addAll(new Inserter(item).options(rate));
        options.addAll(new Pipe(item).options(rate));

        return options;
    }
}

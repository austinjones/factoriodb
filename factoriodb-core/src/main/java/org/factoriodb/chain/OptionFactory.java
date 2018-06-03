package org.factoriodb.chain;

import org.factoriodb.chain.assembler.Assembler;
import org.factoriodb.chain.option.ConnectionOption;
import org.factoriodb.chain.option.RecipeOption;
import org.factoriodb.recipe.RatedRecipe;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author austinjones
 */
public class OptionFactory {
    public static Collection<? extends RecipeOption> recipeOptions(RatedRecipe r) {
        return Assembler.getInstance(r.getRecipe()).options(r);
    }

    public static Collection<? extends ConnectionOption> transitOptions(String item, double rate) {
        Collection<ConnectionOption> options = new ArrayList<>();

        if (isFluid(item)) {
            options.addAll(new Pipe(item).options(rate));
        } else {
            options.addAll(new Belt(item).options(rate));
        }

        return options;
    }

    public static Collection<? extends ConnectionOption> connectionOptions(String item, double rate) {
        Collection<ConnectionOption> options = new ArrayList<>();

        if (isFluid(item)) {
            options.addAll(new Pipe(item).options(rate));
        } else {
            options.addAll(new Belt(item).options(rate));
            options.addAll(new Inserter(item).options(rate));
        }

        return options;
    }

    public static boolean isFluid(String item) {
        // TODO: load from classpath resource, parse out fluid names
        return item.equals("crude-oil")
                || item.equals("heavy-oil")
                || item.equals("light-oil")
                || item.equals("petroleum-gas")
                || item.equals("lubricant")
                || item.equals("sulfuric-acid")
                || item.equals("water")
                || item.equals("steam");
    }
}

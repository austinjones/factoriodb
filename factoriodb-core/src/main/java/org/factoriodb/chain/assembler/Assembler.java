package org.factoriodb.chain.assembler;

import org.factoriodb.chain.option.RecipeOption;
import org.factoriodb.model.CrafterType;
import org.factoriodb.recipe.RatedRecipe;
import org.factoriodb.recipe.Recipe;

import java.util.Collection;

public interface Assembler {
    public static Assembler getInstance(CrafterType type) {
        switch(type) {
            case ASSEMBLER: return new StandardAssembler();
            case ADVANCED_ASSEMBLER: return new AdvancedAssembler();
            case CENTRIFUGE: return new CentrifugeAssembler();
            case CHEMICAL_PLANT: return new ChemicalPlantAssembler();
            case SMELTER: return new SmelterAssembler();
            case OIL_REFINERY: return new OilRefineryAssembler();
            case INPUT: return new IdentityAssembler();
            case OUTPUT: return new IdentityAssembler();
        }

        throw new IllegalArgumentException("Unsupported craftItem type: " + type);
    }

    public static Assembler getInstance(Recipe r) {
        return getInstance(r.crafterType);
    }

    public Collection<? extends RecipeOption> options(RatedRecipe recipe);
}

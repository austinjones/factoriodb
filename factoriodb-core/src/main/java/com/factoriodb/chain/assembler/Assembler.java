package com.factoriodb.chain.assembler;

import com.factoriodb.chain.option.RecipeOption;
import com.factoriodb.graph.RatedRecipe;
import com.factoriodb.graph.Recipe;
import com.factoriodb.model.CrafterType;

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

package com.factoriodb.chain.assembler;

import com.factoriodb.chain.Crafter;
import com.factoriodb.chain.option.CrafterOption;
import com.factoriodb.model.CrafterType;
import com.factoriodb.model.Recipe;

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
        }

        throw new IllegalArgumentException("Unsupported craftItem type: " + type);
    }

    public static Assembler getInstance(Recipe r) {
        return getInstance(r.getCrafterType());
    }

    public Collection<? extends CrafterOption> options(Crafter crafter);
}

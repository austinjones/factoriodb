package com.factoriodb.model;

/**
 * @author austinjones
 */
public enum CrafterType {
    ASSEMBLER,
    ADVANCED_ASSEMBLER,
    SMELTER,
    CHEMICAL_PLANT,
    OIL_REFINERY,
    ROCKET_LAUNCH_SITE,
    CENTRIFUGE;

    public static CrafterType fromInput(String input) {
        if(input == null || input.isEmpty()) {
            return CrafterType.ASSEMBLER;
        }

        switch(input) {
            case "chemistry": return CHEMICAL_PLANT;
            case "oil-processing": return OIL_REFINERY;
            case "crafting": return ASSEMBLER;
            case "crafting-with-fluid": return ASSEMBLER;
            case "advanced-crafting": return ADVANCED_ASSEMBLER;
            case "smelting": return SMELTER;
            case "rocket-building": return ROCKET_LAUNCH_SITE;
            case "centrifuging": return CENTRIFUGE;
        }

        throw new UnsupportedOperationException(input);
    }
}

package com.factoriodb.chain.assembler;

import com.factoriodb.chain.Crafter;
import com.factoriodb.chain.option.AssemblerOption;
import com.factoriodb.chain.option.CrafterOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author austinjones
 */
public class ChemicalPlantAssembler implements Assembler {
    @Override
    public Collection<? extends CrafterOption> options(Crafter crafter) {
        List<AssemblerOption> options = new ArrayList<>();
        options.add(new AssemblerOption(crafter, "chemical-plant", 1.25));
        return options;
    }
}

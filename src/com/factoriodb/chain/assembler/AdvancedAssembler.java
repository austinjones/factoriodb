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
public class AdvancedAssembler implements Assembler {
    @Override
    public Collection<? extends CrafterOption> options(Crafter crafter) {
        List<AssemblerOption> options = new ArrayList<>();
        options.add(new AssemblerOption(crafter, "assembling-machine-2", 0.75));
        options.add(new AssemblerOption(crafter, "assembling-machine-3", 1.25));
        return options;
    }
}

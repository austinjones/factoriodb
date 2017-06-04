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
public class SmelterAssembler implements Assembler {
    @Override
    public Collection<? extends CrafterOption> options(Crafter crafter) {
        List<AssemblerOption> options = new ArrayList<>();
        options.add(new AssemblerOption(crafter, "stone-furnace", 1));
        options.add(new AssemblerOption(crafter, "steel-furnace", 2));
        options.add(new AssemblerOption(crafter, "electric-furnace", 2));
        return options;
    }
}

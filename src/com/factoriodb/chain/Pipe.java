package com.factoriodb.chain;

import com.factoriodb.model.Item;
import com.factoriodb.model.ItemsStack;
import com.factoriodb.model.Model;
import com.factoriodb.chain.option.ConnectionOption;
import com.factoriodb.chain.option.PipeOption;
import com.factoriodb.com.factoriodb.input.InputFluid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author austinjones
 */
public class Pipe extends Connection {
    public static final double SPEED_YELLOW = 13.333;
    public static final double SPEED_RED = 26.666;
    public static final double SPEED_BLUE = 40.0;

    private Item fluid;
    public Pipe(Item item) {
        this.fluid = item;
    }

    public Pipe(Model m, String fluidName) {
        this(m.getItemByName(fluidName));
    }

    public Item getFluid() {
        return fluid;
    }

    @Override
    public ItemsStack getOutputRatio() {
        return new ItemsStack(fluid.name(), 1);
    }

    @Override
    public ItemsStack getOutputRatio(ItemsStack inputRatio) {
        return inputRatio;
    }

    @Override
    public ItemsStack getInputRatio() {
        return new ItemsStack(fluid.name(), 1);
    }
    @Override
    public Collection<? extends ConnectionOption> options() {
        List<PipeOption> options = new ArrayList<>();
        options.add(new PipeOption(this, "pipe"));
        return options;
    }

    @Override
    public String toString() {
        return "pipe[" + fluid + "]";
    }
}

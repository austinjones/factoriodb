package com.factoriodb.chain;

import com.factoriodb.model.Item;
import com.factoriodb.model.ItemsStack;
import com.factoriodb.model.Model;
import com.factoriodb.chain.option.ConnectionOption;
import com.factoriodb.chain.option.PipeOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author austinjones
 */
public class Pipe extends Connection {
    // SPEED FORMULA: Tt = (50*(n-1) + 200) / (3*(n-1) + 2)
    // Link: https://www.reddit.com/r/factorio/comments/6fsjew/pipe_throughput_equation_015/
    public static final double SPEED_YELLOW = 13.333;
    public static final double SPEED_RED = 26.666;
    public static final double SPEED_BLUE = 40.0;

    private String fluid;
    public Pipe(String item) {
        this.fluid = item;
    }

//    public Pipe(Model m, String fluidName) {
//        this(m.getItemByName(fluidName));
//    }

    public String getFluid() {
        return fluid;
    }

    @Override
    public Collection<? extends ConnectionOption> options(double rate) {
        List<PipeOption> options = new ArrayList<>();
        options.add(new PipeOption("pipe", fluid, rate));
        return options;
    }

    @Override
    public String toString() {
        return "pipe[" + fluid + "]";
    }
}

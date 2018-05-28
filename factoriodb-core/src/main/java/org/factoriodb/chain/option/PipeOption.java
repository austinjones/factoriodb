package org.factoriodb.chain.option;

/**
 * @author austinjones
 */
public class PipeOption extends ConnectionOption {
    private static double MAX_FLOW = 200 * 60;

    private String fluid;
    private double rate;
    private double count;

    public PipeOption(String name, String fluid, double rate) {
        super(name);

        this.fluid = fluid;
        this.rate = rate;
        this.count = rate / MAX_FLOW;
    }

    @Override
    public double count() {
        return count;
    }

    @Override
    public double input() {
        return rate;
    }

    @Override
    public double output() {
        return rate;
    }

    @Override
    public double maxInput() {
        return Math.ceil(count) * MAX_FLOW;
    }

    @Override
    public double maxOutput() {
        return Math.ceil(count) * MAX_FLOW;
    }
}

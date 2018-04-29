package com.factoriodb.chain.option;

import com.factoriodb.chain.Belt;
import com.factoriodb.model.ItemsStack;

public class BeltOption extends ConnectionOption {
	private static double MAX_FLOW = 13.33;

	private double beltFlow;
	private double rate;
	private double count;
	
	public BeltOption(String name, double flow, double rate) {
        super(name);
		this.beltFlow = flow;
		this.rate = rate;
		this.count = rate / flow;
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
        return Math.ceil(count) * beltFlow;
    }

    @Override
    public double maxOutput() {
        return Math.ceil(count) * beltFlow;
    }
}

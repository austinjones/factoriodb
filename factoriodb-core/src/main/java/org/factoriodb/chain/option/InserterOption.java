package org.factoriodb.chain.option;

public class InserterOption extends ConnectionOption {

	private double speed;
	private double stackSize;
	private double count;
	private double rate;

	public InserterOption(String name, double speed, int stackSize, double rate) {
        super(name);
		this.speed = speed;
		this.stackSize = stackSize;
		this.rate = rate;
		this.count = rate / (speed * stackSize);
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
        return Math.ceil(count) * speed * stackSize;
    }

    @Override
    public double maxOutput() {
        return Math.ceil(count) * speed * stackSize;
    }
}

package com.factoriodb.chain.option;

public abstract class ConnectionOption extends EntityOption {
	private String optionDescription;
	
	public ConnectionOption(String optionDescription) {
		this.optionDescription = optionDescription;
	}

    @Override
    public String name() {
        return optionDescription;
    }

    public abstract double count();

    public abstract double input();
    public abstract double output();

    public abstract double maxInput();
    public abstract double maxOutput();
//	@Override
//	public List<Item> getOutputRatio() {
//		return self.getOutputRatio();
//	}
//
//	@Override
//	public List<Item> getInputRatio() {
//		return self.getInputRatio();
//	}
//
//	@Override
//	public Collection<? extends ConnectionOption> options() {
//		return self.options();
//	}
	
	public String toString() {
        return optionDescription + " x " + this.count();
    }
}

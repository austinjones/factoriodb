package com.factoriodb.chain.option;

import com.factoriodb.chain.Crafter;

public abstract class CrafterOption extends EntityOption {
	private String optionDescription;
	private Crafter self;
	
	public CrafterOption(Crafter self, String optionDescription) {
		this.self = self;
		this.optionDescription = optionDescription;
	}

    @Override
    public String name() {
        return optionDescription;
    }
//	public abstract ItemsFlow outputFlow(ItemsFlow input);
//
//	public abstract ItemsFlow requestedInput();
	
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
//	public Collection<? extends CrafterOption> options() {
//		return self.options();
//	}
	
	public String toString() {
		return optionDescription;
	}
	
	public boolean isCrafter() {
		return true;
	}
}

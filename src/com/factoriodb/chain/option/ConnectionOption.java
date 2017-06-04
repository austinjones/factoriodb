package com.factoriodb.chain.option;

import com.factoriodb.chain.Connection;
import com.factoriodb.model.ItemsStack;

public abstract class ConnectionOption extends EntityOption {
	private String optionDescription;
	private Connection self;
	
	public ConnectionOption(Connection self, String optionDescription) {
		this.self = self;
		this.optionDescription = optionDescription;
	}

    @Override
    public String name() {
        return optionDescription;
    }

	@Override
	public ItemsStack availableOutputLimited(ItemsStack requestedOutput, ItemsStack input) {
		ItemsStack best = availableOutputLimited(requestedOutput);
		return best.throttle(input);
	}
	
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
		return optionDescription;
	}
	
	@Override
	public boolean isConnection() {
		return true;
	}
}

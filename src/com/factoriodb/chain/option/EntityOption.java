package com.factoriodb.chain.option;

import com.factoriodb.model.ItemsFlow;

public abstract class EntityOption {
	public abstract ItemsFlow requestedInputLimited(ItemsFlow output);
	public abstract ItemsFlow availableOutputLimited(ItemsFlow output);
	public abstract ItemsFlow availableOutputLimited(ItemsFlow requestedOutput, ItemsFlow input);
	
	public boolean isConnection() {
		return false;
	}
	
	public boolean isCrafter() {
		return false;
	}
}

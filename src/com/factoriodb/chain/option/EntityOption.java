package com.factoriodb.chain.option;

import com.factoriodb.model.ItemsStack;

public abstract class EntityOption {
    public abstract String name();
	public abstract ItemsStack requestedInputLimited(ItemsStack output);
	public abstract ItemsStack availableOutputLimited(ItemsStack output);
	public abstract ItemsStack availableOutputLimited(ItemsStack requestedOutput, ItemsStack input);
    public abstract double constructionCost();
    public abstract double placementCost();
	public abstract double maxInput();
    public abstract double maxOutput();

	public boolean isConnection() {
		return false;
	}
	
	public boolean isCrafter() {
		return false;
	}
}

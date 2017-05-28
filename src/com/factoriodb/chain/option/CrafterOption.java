package com.factoriodb.chain.option;

import com.factoriodb.chain.Crafter;
import com.factoriodb.model.ItemsFlow;

public abstract class CrafterOption extends EntityOption {
	private String optionDescription;
	private Crafter self;
	
	public CrafterOption(Crafter self, String optionDescription) {
		this.self = self;
		this.optionDescription = optionDescription;
	}
	
//	public abstract ItemsFlow outputFlow(ItemsFlow input);
//
//	public abstract ItemsFlow requestedInput();
	
//	@Override
//	public List<Item> getAvailableItems() {
//		return self.getAvailableItems();
//	}
//
//	@Override
//	public List<Item> getRequestedItems() {
//		return self.getRequestedItems();
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

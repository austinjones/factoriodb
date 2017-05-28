package com.factoriodb.chain.option;

import java.util.Collection;
import java.util.List;

import com.factoriodb.chain.Connection;
import com.factoriodb.model.Item;
import com.factoriodb.model.ItemsFlow;

public abstract class ConnectionOption extends EntityOption {
	private String optionDescription;
	private Connection self;
	
	public ConnectionOption(Connection self, String optionDescription) {
		this.self = self;
		this.optionDescription = optionDescription;
	}
	
	@Override
	public ItemsFlow availableOutputLimited(ItemsFlow requestedOutput, ItemsFlow input) {
		ItemsFlow best = availableOutputLimited(requestedOutput);
		return best.throttle(input);
	}
	
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

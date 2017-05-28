package com.factoriodb.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.factoriodb.chain.option.BeltOption;
import com.factoriodb.chain.option.ConnectionOption;
import com.factoriodb.chain.option.CrafterOption;
import com.factoriodb.model.Item;

public class Belt extends Connection {
	private Item item;
	public Belt(Item item) {
		this.item = item;
	}
	
	public Item getItem() {
		return item;
	}

	@Override
	public List<Item> getAvailableItems() {
		return Collections.singletonList(item);
	}

	@Override
	public List<Item> getRequestedItems() {
		return Collections.singletonList(item);
	}

	@Override
	public Collection<? extends ConnectionOption> options() {
		List<BeltOption> options = new ArrayList<>();
		options.add(new BeltOption(this, "transport-belt-1", 13.333));
		options.add(new BeltOption(this, "transport-belt-2", 26.666));
		options.add(new BeltOption(this, "transport-belt-3", 40.0));
		return options;
	}
	
	@Override
	public String toString() {
		return "belt[" + item.name + "]";
	}
}

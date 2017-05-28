package com.factoriodb.chain;

import java.util.List;

import com.factoriodb.model.Item;

public interface InserterTarget {
	public List<Item> getRequestedItems();
}

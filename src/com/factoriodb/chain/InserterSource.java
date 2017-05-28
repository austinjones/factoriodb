package com.factoriodb.chain;

import java.util.List;

import com.factoriodb.model.Item;

public interface InserterSource {
	public List<Item> getAvailableItems();
}

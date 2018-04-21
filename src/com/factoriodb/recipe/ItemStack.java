package com.factoriodb.recipe;

import com.factoriodb.input.InputItem;

public class ItemStack {
	private InputItem item;
	private int quantity;
	
	public ItemStack(InputItem item, int quantity) {
		this.item = item;
		this.quantity = quantity;
	}

	public InputItem getItem() {
		return item;
	}

	public int getQuantity() {
		return quantity;
	}
}

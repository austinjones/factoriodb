package com.factoriodb.input;

public class InputRecipeItem {
	public String name;
	public String type;
	public int amount;

    public InputRecipeItem() {};
    public InputRecipeItem(String s, int i) {
        this.name = s;
        this.amount = i;
    }
}
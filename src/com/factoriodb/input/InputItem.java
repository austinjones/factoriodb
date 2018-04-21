package com.factoriodb.input;

import java.util.ArrayList;
import java.util.List;

public class InputItem {
	public String name;
	public String icon;
	public List<String> flags = new ArrayList<>();
	public String subgroup;
	public String order;
	public String place_result;
	public int stack_size;

    public InputItem() {}

    public InputItem(String name, int stack_size) {
        this.name = name;
        this.stack_size = stack_size;
    }

	@Override
	public String toString() {
		return name;
	}
}
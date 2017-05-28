package com.factoriodb.model;

import java.util.ArrayList;
import java.util.List;

public class Item {
	public String name;
	public String icon;
	public List<String> flags = new ArrayList<>();
	public String subgroup;
	public String order;
	public String place_result;
	public int stack_size;
	
	@Override
	public String toString() {
		return name;
	}
}
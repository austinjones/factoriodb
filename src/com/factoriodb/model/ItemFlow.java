package com.factoriodb.model;

public class ItemFlow {
	private String item;
	private double flow;
	public ItemFlow(String item, double flowRate) {
		this.item = item;
		this.flow = flowRate;
	}
	
	public String item() {
		return item;
	}
	
	public double flow() {
		return flow;
	}
	
	public String toString() {
		return item + " x " + flow;
	}
}

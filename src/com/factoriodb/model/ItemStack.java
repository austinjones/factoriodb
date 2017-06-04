package com.factoriodb.model;

public class ItemStack {
	private String item;
	private double count;
	public ItemStack(String item, double flowRate) {
		this.item = item;
		this.count = flowRate;
	}
	
	public String name() {
		return item;
	}
	
	public double amount() {
		return count;
	}

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof ItemStack)) {
            return false;
        }

        ItemStack other = (ItemStack)obj;
        return item.equals(other.item) && count == other.count;
    }

    public String toString() {
        return String.format("%-2.2f/s %s", count, item);
	}
}

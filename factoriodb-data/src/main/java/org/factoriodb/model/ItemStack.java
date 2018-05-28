package org.factoriodb.model;

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
        // TODO: really bad hack here.  getting floating point rounding errors in SolverTest.
        return item.equals(other.item) && Math.abs(count - other.count) < 0.00001;
    }

    public String toString() {
        return String.format("%-2.2f/s %s", count, item);
	}
}

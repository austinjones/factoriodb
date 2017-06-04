package com.factoriodb.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.factoriodb.model.Item;
import com.factoriodb.model.ItemsStack;
import com.factoriodb.model.Model;
import com.factoriodb.chain.option.BeltOption;
import com.factoriodb.chain.option.ConnectionOption;

public class Belt extends Connection {
    public static final double SPEED_YELLOW = 13.333;
    public static final double SPEED_RED = 26.666;
    public static final double SPEED_BLUE = 40.0;

	private Item item;
	public Belt(Item item) {
		this.item = item;
	}

    public Belt(Model m, String itemName) {
        this(m.getItemByName(itemName));
    }

    public Item getItem() {
		return item;
	}

	@Override
	public ItemsStack getOutputRatio() {
		return new ItemsStack(item.name(), 1);
	}

    @Override
    public ItemsStack getOutputRatio(ItemsStack inputRatio) {
        return inputRatio;
    }

    @Override
	public ItemsStack getInputRatio() {
        return new ItemsStack(item.name(), 1);
	}

	@Override
	public Collection<? extends ConnectionOption> options() {
		List<BeltOption> options = new ArrayList<>();
		options.add(new BeltOption(this, "transport-belt-1", SPEED_YELLOW));
		options.add(new BeltOption(this, "transport-belt-2", SPEED_RED));
		options.add(new BeltOption(this, "transport-belt-3", SPEED_BLUE));
		return options;
	}
	
	@Override
	public String toString() {
		return "belt[" + item + "]";
	}
}

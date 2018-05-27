package com.factoriodb.chain;

import com.factoriodb.chain.option.BeltOption;
import com.factoriodb.chain.option.ConnectionOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Belt {
    public static final double SPEED_YELLOW = 13.333;
    public static final double SPEED_RED = 26.666;
    public static final double SPEED_BLUE = 40.0;

	private String item;
	public Belt(String item) {
		this.item = item;
	}

//    public Belt(Model m, String itemName) {
//        this(m.getItemByName(itemName));
//    }

    public String getItem() {
		return item;
	}

	public Collection<? extends ConnectionOption> options(double rate) {
		List<BeltOption> options = new ArrayList<>();
		options.add(new BeltOption("transport-belt-1", SPEED_YELLOW, rate));
		options.add(new BeltOption("transport-belt-2", SPEED_RED, rate));
		options.add(new BeltOption("transport-belt-3", SPEED_BLUE, rate));
		return options;
	}
	
	@Override
	public String toString() {
		return "belt[" + item + "]";
	}
}

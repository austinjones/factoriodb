package com.factoriodb.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.factoriodb.chain.option.ConnectionOption;
import com.factoriodb.chain.option.InserterOption;
import com.factoriodb.model.ItemsStack;

public class Inserter extends Connection {
    private String item;
	public Inserter(String item) {
        this.item = item;
	}

	public Collection<? extends ConnectionOption> options(double rate) {
		List<InserterOption> options = new ArrayList<>();
		options.add(new InserterOption("inserter", 0.83, 1, rate));
		options.add(new InserterOption("long-handed-inserter", 1.15, 1, rate));
		options.add(new InserterOption("fast-inserter", 2.31, 1, rate));
		options.add(new InserterOption("stack-inserter", 2.31, 2, rate));
		return options;
	}
	
	@Override
	public String toString() {
		return "inserter[" + item + "]";
	}
}

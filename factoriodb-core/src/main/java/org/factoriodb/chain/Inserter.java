package org.factoriodb.chain;

import org.factoriodb.chain.option.ConnectionOption;
import org.factoriodb.chain.option.InserterOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

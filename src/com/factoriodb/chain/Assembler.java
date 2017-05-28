package com.factoriodb.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.factoriodb.chain.option.AssemblerOption;
import com.factoriodb.chain.option.CrafterOption;
import com.factoriodb.model.Item;
import com.factoriodb.model.Recipe;

public class Assembler extends Crafter {
	private Item i;
	private Recipe r;
	
	public Assembler(Item item, Recipe recipe) {
		this.i = item;
		this.r = recipe;
	}
	
	public Recipe getRecipe() {
		return r;
	}
	
	public Item getItem() {
		return i;
	}

	@Override
	public List<Item> getRequestedItems() {
		//TODO: implement
		return new ArrayList<>();
	}

	@Override
	public List<Item> getAvailableItems() {
		return Collections.singletonList(i);
	}

	@Override
	public Collection<? extends CrafterOption> options() {
		List<AssemblerOption> options = new ArrayList<>();
		options.add(new AssemblerOption(this, "assembling-machine-1", 0.5));
		options.add(new AssemblerOption(this, "assembling-machine-2", 0.75));
		options.add(new AssemblerOption(this, "assembling-machine-3", 1.25));
		return options;
	}
	
	@Override
	public String toString() {
		return "assembler[" + r.result + " x" + r.result_count + "]";
	}
}

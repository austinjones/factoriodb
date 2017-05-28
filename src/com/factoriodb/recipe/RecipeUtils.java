package com.factoriodb.recipe;

import java.util.List;

import com.factoriodb.Model;
import com.factoriodb.chain.Assembler;
import com.factoriodb.chain.Belt;
import com.factoriodb.model.Item;
import com.factoriodb.model.Recipe;

public class RecipeUtils {
	private Model m;
	public RecipeUtils(Model m) {
		this.m = m;
	}
	
	public Assembler assembler(String item) {
		List<Recipe> rs = m.getRecipeByResult(item);
		if(rs == null || rs.isEmpty()) {
			throw new NullPointerException("Unknown recipe for item " + item);
		}
		
		Item i = m.getItemByName(item);
		if(i == null) {
			throw new NullPointerException("Unknown item " + item);
		}
		
		return new Assembler(i, rs.get(0));
	}

	public Belt belt(String itemName) {
		Item item = m.getItemByName(itemName);
		if(item == null) {
			throw new NullPointerException("Unknown item " + itemName);
		}
		return new Belt(item);
	}

//	public SplitBelt splitbelt(String left, String right) {
//		Item l = m.getItemByName(left);
//		if(l == null) {
//			throw new NullPointerException("Unknown item " + left);
//		}
//		
//		Item r = m.getItemByName(right);
//		if(r == null) {
//			throw new NullPointerException("Unknown item " + right);
//		}
//		
//		return new SplitBelt(l, r);
//	}
}

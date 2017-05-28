package com.factoriodb.chain.option;

import com.factoriodb.chain.Assembler;
import com.factoriodb.model.Item;
import com.factoriodb.model.ItemsFlow;
import com.factoriodb.model.Recipe;
import com.factoriodb.model.RecipeIngredient;

public class AssemblerOption extends CrafterOption {
	private static double SPEED_1 = 0.5;
	private Assembler assembler;
	private double speed;
	
	public AssemblerOption(Assembler entity, String name, double speed) {
		super(entity, name);
		
		this.assembler = entity;
		this.speed = speed;
	}

	public enum AssemblerType {
		
	}
	
//	@Override
//	public ItemsFlow outputFlow(ItemsFlow input) {
//		Item i = assembler.getItem();
//		Recipe r = assembler.getRecipe();
//		
//		double minThroughput = Double.MAX_VALUE;
//		
//		for(RecipeIngredient ingredient : r.ingredients) {
//			double flow = input.getDouble(ingredient.name);
//			double required = speed * ingredient.amount / r.energy_required;
//			double throughput = flow / required;
//			if(throughput < minThroughput) {
//				minThroughput = throughput;
//			}
//		}
//		
//		if(minThroughput == Double.MAX_VALUE) {
//			return new ItemsFlow();
//		}
//		
//		if(minThroughput > 1) {
//			minThroughput = 1;
//		}
//		
//		double flow = speed * minThroughput * r.result_count / r.energy_required;
//		return new ItemsFlow(i.name, flow);
//	}

	@Override
	public ItemsFlow requestedInputLimited(ItemsFlow requestedOutput) {
		Item i = assembler.getItem();
		Recipe r = assembler.getRecipe();
		
		double flow = requestedOutput.getDouble(i.name);
		double rate = flow / r.result_count; 
		
		return requestedInput(rate);
	}

//	@Override
//	public ItemsFlow requestedInput() {
//		return requestedInput(1.0);
//	}
	
	private ItemsFlow requestedInput(double rate) {
		Item i = assembler.getItem();
		Recipe r = assembler.getRecipe();
		
		ItemsFlow request = new ItemsFlow();
		for(RecipeIngredient ing : r.ingredients) {
			double ingredientFlow = speed * rate * ing.amount / r.energy_required;
			request = ItemsFlow.add(request, new ItemsFlow(ing.name, ingredientFlow));
		}

		return request;
	}

	@Override
	public ItemsFlow availableOutputLimited(ItemsFlow output) {
		Item i = assembler.getItem();
		Recipe r = assembler.getRecipe();
		
		double rate = speed * r.result_count / r.energy_required;
		return new ItemsFlow(i.name, rate);
	}

	@Override
	public ItemsFlow availableOutputLimited(ItemsFlow requestedOutput, ItemsFlow input) {
		Item i = assembler.getItem();
		Recipe r = assembler.getRecipe();
		
		double minThroughput = Double.MAX_VALUE;
		
		for(RecipeIngredient ingredient : r.ingredients) {
			double flow = input.getDouble(ingredient.name);
			double required = speed * ingredient.amount / r.energy_required;
			double throughput = flow / required;
			if(throughput < minThroughput) {
				minThroughput = throughput;
			}
		}
		
		if(minThroughput == Double.MAX_VALUE) {
			return new ItemsFlow();
		}
		
		if(minThroughput > 1) {
			minThroughput = 1;
		}
		
		double flow = speed * minThroughput * r.result_count / r.energy_required;
		return new ItemsFlow(i.name, flow);
	}
}

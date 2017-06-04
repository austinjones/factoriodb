package com.factoriodb.chain.option;

import com.factoriodb.chain.Crafter;
import com.factoriodb.model.ItemStack;
import com.factoriodb.model.ItemsStack;
import com.factoriodb.model.Recipe;

public class AssemblerOption extends CrafterOption {
	private static double SPEED_1 = 0.5;
	private Crafter crafter;
	private double speed;
	
	public AssemblerOption(Crafter entity, String name, double speed) {
		super(entity, name);
		
		this.crafter = entity;
		this.speed = speed;
	}
	
//	@Override
//	public ItemsFlow outputFlow(ItemsFlow input) {
//		Item i = assembler.getItem();
//		Recipe r = assembler.getRecipe();
//		
//		double minThroughput = Double.MAX_VALUE;
//		
//		for(RecipeIngredient ingredient : r.ingredients) {
//			double amount = input.getDouble(ingredient.name);
//			double required = speed * ingredient.amount / r.energy_required;
//			double throughput = amount / required;
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
//		double amount = speed * minThroughput * r.result_count / r.energy_required;
//		return new ItemsFlow(i.name, amount);
//	}

	@Override
	public ItemsStack requestedInputLimited(ItemsStack requestedOutput) {
		Recipe r = crafter.getRecipe();

        double maxRate = 0;
        for(ItemStack item : r.getOutput()) {
            double flow = requestedOutput.getDouble(item.name());
            double rate = flow / (speed * item.amount() / r.getEnergyRequired());
            if(rate > maxRate) {
                maxRate = rate;
            }
        }

		return requested(maxRate);
	}

//	@Override
//	public ItemsFlow requested() {
//		return requested(1.0);
//	}
	
	private ItemsStack requested(double rate) {
		Recipe r = crafter.getRecipe();
		
		ItemsStack request = new ItemsStack();
		for(ItemStack ing : r.getInput()) {
			double ingredientFlow = speed * rate * ing.amount() / r.getEnergyRequired();
			request = ItemsStack.add(request, new ItemsStack(ing.name(), ingredientFlow));
		}

		return request;
	}

	@Override
	public ItemsStack availableOutputLimited(ItemsStack output) {
		Recipe r = crafter.getRecipe();
        double rate = speed / r.getEnergyRequired();
		return ItemsStack.mul(r.getOutput(), rate);
	}

	@Override
	public ItemsStack availableOutputLimited(ItemsStack requestedOutput, ItemsStack input) {
		Recipe r = crafter.getRecipe();
		
		double minThroughput = Double.MAX_VALUE;
		
		for(ItemStack ingredient : r.getInput()) {
			double flow = input.getDouble(ingredient.name());
			double required = speed * ingredient.amount() / r.getEnergyRequired();
			double throughput = flow / required;
			if(throughput < minThroughput) {
				minThroughput = throughput;
			}
		}
		
		if(minThroughput == Double.MAX_VALUE) {
			return new ItemsStack();
		}
		
		if(minThroughput > 1) {
			minThroughput = 1;
		}
		
		double rate = speed * minThroughput / r.getEnergyRequired();
		return ItemsStack.mul(r.getOutput(), rate);
	}

    @Override
    public double constructionCost() {
        return speed;
    }

    @Override
    public double placementCost() {
        return 1;
    }

    @Override
    public double maxInput() {
        return requested(1.0).total();
    }

    @Override
    public double maxOutput() {
        return availableOutputLimited(null).total();
    }
}

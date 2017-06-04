package com.factoriodb.chain.option;

import com.factoriodb.chain.Inserter;
import com.factoriodb.model.ItemsStack;

public class InserterOption extends ConnectionOption {
	
	private Inserter inserter;
	private double speed;
	private double stackSize;
	
	public InserterOption(Inserter entity, String name, double speed, int stackSize) {
		super(entity, name);
		
		this.inserter = entity;
		this.speed = speed;
		this.stackSize = stackSize;
	}

	@Override
	public ItemsStack requestedInputLimited(ItemsStack output) {
        if (output.total() < speed * stackSize) {
            return output;
        }

        double scale = (speed * stackSize) / output.total();
		return ItemsStack.mul(output, scale);
	}

	@Override
	public ItemsStack availableOutputLimited(ItemsStack output) {
		double total = output.total();
		double inserterFlow = speed * stackSize;
		
		if (total < inserterFlow) {
			return output;
		} else {
			return ItemsStack.mul(output, inserterFlow / total);
		}
	}

    @Override
    public double constructionCost() {
        return speed * stackSize;
    }

    @Override
    public double placementCost() {
        return 1;
    }

    @Override
    public double maxInput() {
        return speed * stackSize;
    }

    @Override
    public double maxOutput() {
        return speed * stackSize;
    }

//	@Override
//	public ItemsFlow outputFlow(ItemsFlow inputs) {
//		ItemsFlow targetFlow = target.requestedInput();
//		
//		double totalTargetFlow = targetFlow.total();
//		double totalInputFlow = inputs.total();
//		double inputUsage = 1;
//		
//		// balance available supply among itemflow targets
//		for(ItemFlow i : inputs.items()) {
//			double irate = targetFlow.getDouble(i.name()) / i.amount();
//			if(irate < inputUsage) {
//				inputUsage = irate;
//			}
//		}
//		
//		double insertionRate = Math.min(speed * stackSize, inputUsage * totalInputFlow);
//		
//		ItemsFlow outputRate = new ItemsFlow();
//		for (ItemFlow i : targetFlow.items()) {
//			double itemProportion = i.amount() / totalTargetFlow;
//			double itemRate = insertionRate * itemProportion;
//			outputRate = ItemsFlow.add(outputRate, new ItemsFlow(i.name(), itemRate));
//		}
//		
//		return outputRate;
//	}
//
//	@Override
//	public ItemsFlow requestedInputLimited(ItemsFlow output) {
//		// TODO: better implementation.  account for target throughput limit and inserter limit
//		return output;
//	}
//
//	@Override
//	public ItemsFlow requestedInput() {
//		return target.requestedInput();
//	}
//
//	@Override
//	public ItemsFlow availableOutputLimited(ItemsFlow output) {
//		double total = output.total();
//		double inserterFlow = speed * stackSize;
//		
//		if (total < inserterFlow) {
//			return output;
//		} else {
//			return ItemsFlow.mul(output, inserterFlow / total);
//		}
//	}
}

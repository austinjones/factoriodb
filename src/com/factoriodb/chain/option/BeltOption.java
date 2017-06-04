package com.factoriodb.chain.option;

import com.factoriodb.chain.Belt;
import com.factoriodb.model.ItemsStack;

public class BeltOption extends ConnectionOption {
	private static double MAX_FLOW = 13.33;

	private Belt belt;
	private double beltFlow;
	private ItemsStack filter;
	
	public BeltOption(Belt entity, String name, double flow) {
		super(entity, name);
		
		this.belt = entity;
		this.beltFlow = flow;
		this.filter = new ItemsStack(belt.getItem().name(), beltFlow);
	}

	public enum BeltType {
		
	}
	
	@Override
	public ItemsStack requestedInputLimited(ItemsStack output) {
		return output.throttle(filter);
	}

//	@Override
//	public ItemsFlow availableOutputLimited(ItemsFlow output) {
//		double total = output.total();
//		
//		if (total < beltFlow) {
//			return output.throttle(filter);
//		} else {
//			return ItemsFlow.mul(output, beltFlow / total)
//						.throttle(filter);
//		}
//	}
	@Override
	public ItemsStack availableOutputLimited(ItemsStack output) {
		return output.throttle(filter);
	}

    @Override
    public double constructionCost() {
        return beltFlow;
    }

    @Override
    public double placementCost() {
        return 1;
    }

    @Override
    public double maxInput() {
        return beltFlow;
    }

    @Override
    public double maxOutput() {
        return beltFlow;
    }


//	@Override
//	public ItemsFlow outputFlow(ItemsFlow input) {
//		Item name = belt.getItem();
//		
//		double inputFlow = input.getDouble(name.name);
//		double outputFlow = Math.min(inputFlow, beltFlow);
//		return new ItemsFlow(name.name, outputFlow);
//	}
//
//	@Override
//	public ItemsFlow requestedInputLimited(ItemsFlow output) {
//		Item name = belt.getItem();
//		
//		double amount = Math.min(output.get(name.name).amount(), beltFlow);
//		return new ItemsFlow(name.name, amount);
//	}
//
//	@Override
//	public ItemsFlow requestedInput() {
//		Item name = belt.getItem();
//		
//		return new ItemsFlow(name.name, beltFlow);
//	}
//
//	@Override
//	public ItemsFlow availableOutputLimited(ItemsFlow output) {
//		double total = output.total();
//		
//		if (total < beltFlow) {
//			return output;
//		} else {
//			return ItemsFlow.mul(output, beltFlow / total);
//		}
//	}
}

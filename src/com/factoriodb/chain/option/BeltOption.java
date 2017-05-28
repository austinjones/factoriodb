package com.factoriodb.chain.option;

import com.factoriodb.chain.Belt;
import com.factoriodb.model.ItemsFlow;

public class BeltOption extends ConnectionOption {
	private static double MAX_FLOW = 13.33;

	private Belt belt;
	private double beltFlow;
	private ItemsFlow filter;
	
	public BeltOption(Belt entity, String name, double flow) {
		super(entity, name);
		
		this.belt = entity;
		this.beltFlow = flow;
		this.filter = new ItemsFlow(belt.getItem().name, beltFlow);
	}

	public enum BeltType {
		
	}
	
	@Override
	public ItemsFlow requestedInputLimited(ItemsFlow output) {
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
	public ItemsFlow availableOutputLimited(ItemsFlow output) {
		return output.throttle(filter);
	}


//	@Override
//	public ItemsFlow outputFlow(ItemsFlow input) {
//		Item item = belt.getItem();
//		
//		double inputFlow = input.getDouble(item.name);
//		double outputFlow = Math.min(inputFlow, beltFlow);
//		return new ItemsFlow(item.name, outputFlow);
//	}
//
//	@Override
//	public ItemsFlow requestedInputLimited(ItemsFlow output) {
//		Item item = belt.getItem();
//		
//		double flow = Math.min(output.get(item.name).flow(), beltFlow);
//		return new ItemsFlow(item.name, flow);
//	}
//
//	@Override
//	public ItemsFlow requestedInput() {
//		Item item = belt.getItem();
//		
//		return new ItemsFlow(item.name, beltFlow);
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

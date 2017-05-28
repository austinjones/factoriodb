package com.factoriodb.chain.option;

import com.factoriodb.model.ItemsFlow;

public class ReplicatedOption extends EntityOption {
	private EntityOption source;
	private int count;
	
	public ReplicatedOption(EntityOption source, int count) {
		this.source = source;
		this.count = count;
	}
	
	@Override
	public ItemsFlow requestedInputLimited(ItemsFlow output) {
		ItemsFlow inputFlow = source.requestedInputLimited(output);
		ItemsFlow multiplied = ItemsFlow.mul(inputFlow, count);
		if(source.isCrafter()) {
			return multiplied;
		} else {
			return inputFlow;
		}
	}

	@Override
	public ItemsFlow availableOutputLimited(ItemsFlow output) {
		ItemsFlow sourceFlow = source.availableOutputLimited(output);
		return ItemsFlow.mul(sourceFlow, count);
	}

	@Override
	public ItemsFlow availableOutputLimited(ItemsFlow requestedOutput, ItemsFlow input) {
		ItemsFlow sourceFlow = source.availableOutputLimited(requestedOutput, input);
		ItemsFlow multiplied = ItemsFlow.mul(sourceFlow, count);
		if(source.isConnection()) {
			return multiplied.throttle(input);
		} else {
			return multiplied;
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return source.toString() + " x" + count;
	}
}

package com.factoriodb.chain.option;

import com.factoriodb.model.ItemsStack;

public class ReplicatedOption extends EntityOption {
	private EntityOption source;
	private int count;
	
	public ReplicatedOption(EntityOption source, int count) {
		this.source = source;
		this.count = count;
	}

    @Override
    public String name() {
        return count + "x " + source.name();
    }

    @Override
	public ItemsStack requestedInputLimited(ItemsStack output) {
        if(count == 0) {
            return new ItemsStack();
        }

        ItemsStack one = ItemsStack.div(output, count);
		ItemsStack inputFlow = source.requestedInputLimited(one);
        ItemsStack result = ItemsStack.mul(inputFlow, count);
        return result;
	}

	@Override
	public ItemsStack availableOutputLimited(ItemsStack output) {
        if(count == 0) {
            return new ItemsStack();
        }

        ItemsStack one = ItemsStack.div(output, count);
		ItemsStack sourceFlow = source.availableOutputLimited(one);
        ItemsStack result = ItemsStack.mul(sourceFlow, count);
        return result;
	}

	@Override
	public ItemsStack availableOutputLimited(ItemsStack requestedOutput, ItemsStack input) {
        if(count == 0) {
            return new ItemsStack();
        }

        ItemsStack oneRequestedOutput = ItemsStack.div(requestedOutput, count);
        ItemsStack oneInput = ItemsStack.div(input, count);

        ItemsStack sourceFlow = source.availableOutputLimited(oneRequestedOutput, oneInput);
        ItemsStack result = ItemsStack.mul(sourceFlow, count);
        return result;


//        ItemsFlow multiplied = ItemsFlow.mul(sourceFlow, amount);
//        if(source.isConnection()) {
//            return multiplied.throttle(input);
//        } else {
//            return multiplied;
//        }
	}

    @Override
    public double constructionCost() {
        return count * source.constructionCost();
    }

    @Override
    public double placementCost() {
        return count * source.placementCost();
    }

    @Override
    public double maxInput() {
        return count * source.maxInput();
    }

    @Override
    public double maxOutput() {
        return count * source.maxOutput();
    }

    @Override
	public String toString() {
		return String.format("%-2s %s", count + "x", source);

	}
}

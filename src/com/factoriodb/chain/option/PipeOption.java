package com.factoriodb.chain.option;

import com.factoriodb.chain.Pipe;
import com.factoriodb.model.ItemsStack;

/**
 * @author austinjones
 */
public class PipeOption extends ConnectionOption {
    private static double MAX_FLOW = 200 * 60;

    private Pipe pipe;
    private double pipeFlow = MAX_FLOW;
    private ItemsStack filter;

    public PipeOption(Pipe entity, String name) {
        super(entity, name);

        this.pipe = entity;
        this.filter = new ItemsStack(pipe.getFluid().name(), MAX_FLOW);
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
        return pipeFlow;
    }

    @Override
    public double placementCost() {
        return 1;
    }

    @Override
    public double maxInput() {
        return pipeFlow;
    }

    @Override
    public double maxOutput() {
        return pipeFlow;
    }
}

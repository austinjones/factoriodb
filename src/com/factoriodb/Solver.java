package com.factoriodb;

import java.util.ArrayList;
import java.util.List;

import com.factoriodb.chain.Entity;
import com.factoriodb.chain.option.EntityOption;
import com.factoriodb.model.ItemsStack;
import com.factoriodb.model.Model;

public class Solver {
	private Model model;
	private Entity output;
	
	public Solver(Model model, Entity output) {
		this.model = model;
		this.output = output;
	}

    public static class RatioNode {
        public Entity entity;
        public ItemsStack adjustedInput;
        public ItemsStack adjustedOutput;
        public List<RatioNode> sources = new ArrayList<>();
    }

    public RatioNode solveRatio(Entity entity) {
        RatioNode node = new RatioNode();
        node.entity = entity;

        if(entity.isInput()) {
            node.adjustedInput = entity.getInputRatio();
            node.adjustedOutput = entity.getOutputRatio();
            return node;
        }

        ItemsStack input = new ItemsStack();
        for (Entity source : entity.getInputs()) {
            RatioNode sourceNode = solveRatio(source);
            node.sources.add(sourceNode);

            input = ItemsStack.add(input, sourceNode.adjustedOutput);
        }

        node.adjustedOutput = entity.getOutputRatio(input);
        return node;
    }
	
	public static class SolveOption {
		public EntityOption option;
        public double usageRatio;
        public ItemsStack inputRequest;
		public ItemsStack input;
		public ItemsStack output;
        public boolean unnecessary = false;
		
		@Override
		public String toString() {
            return String.format("%3.0f%% %-25s => %s from %s", 100*usageRatio, option, output, input);
		}
	}
	
	public static class SolveNode {
		public Entity entity;
		public List<SolveOption> options = new ArrayList<>();
		public List<SolveNode> sources = new ArrayList<>();
		
		@Override
		public String toString() {
			return entity.toString();
		}
	}
	
//	public SolveNode solve(Entity entity) {
//		SolveNode result = solveForInput(entity);
//		reduceToInput(result, result.input);
//		return result;
//	}
	
//	private SolveNode constrainOutput(Entity entity, ItemsFlow maxOutput) {
//		SolveNode result = new SolveNode();
//		EntityOption result = entity.optionAboveOutput(maxOutput);
//		if (maxInput.anyMatch((m) -> m.amount() < maxInput.getDouble(m.name()))) {
//			for(Entity source : entity.getInputs()) {
//				SolveNode sourceNode = constrainOutput(source, maxInput);
//				remainingInput = ItemsFlow.sub(remainingInput, sourceNode.output);
//			}
//		}
//		
//		result.input = maxInput;
//		result.option = option;
//		result.output = option.isInput() ? option.requestedFlow() : option.outputFlow(input);
//		
//		return result;
//	}
//
//	public SolveNode solveForInput(Entity entity) {
//		SolveNode result = new SolveNode();
//		
//		ItemsFlow input = new ItemsFlow();
//		for(Entity source : entity.getInputs()) {
//			SolveNode sourceNode = solveForInput(source);
//			input = ItemsFlow.add(input, sourceNode.output);
//		}
//		
//		EntityOption option = entity.optionAboveInput(input);
//		ItemsFlow maxInput = option.requestedFlow();
//		for(Entity source : entity.getInputs()) {
//			SolveNode trueSource = constrainOutput(source, maxInput);
//		}
//		
//		result.option = option;
//		result.input = input;
//		result.output = entity.isInput() ? option.requestedFlow() : option.outputFlow(input);
//		return result;
//	}
	
//	private void reduceToInput(SolveNode node, ItemsFlow remainingInput) {
//		ItemsFlow availableInput = remainingInput;
//		for(SolveNode source : node.sources) {
//			reduceToInput(source, availableInput);
//			availableInput = ItemsFlow.sub(availableInput, source.output);
//		}
//	}
	
//	public SolveNode solveForOutput(Entity entity, ItemsFlow requestedOutput) {
//		SolveNode node = new SolveNode();
//		
//		ItemsFlow input = new ItemsFlow();
////		// pick an output option that can satisfy the output
//		EntityOption option = entity.optionAboveOutput(requestedOutput);
//		
////		// ask the option how much input it needs.
//		ItemsFlow request = option.requestedInputLimited(requestedOutput);
//		ItemsFlow requestRemaining = request;
//		
////		System.out.println(option + " - requesting " + request);
//		
//		for(Entity source : entity.getInputs()) {
//			// recursively solve for the output of sources
//			SolveNode childNode = solveForOutput(source, request);
//			requestRemaining = ItemsFlow.sub(requestRemaining, childNode.output);
//			input = ItemsFlow.add(input, childNode.output);
//			node.sources.add(node);
//		}
//		
////		if(!entity.isInput() && !ItemsFlow.gt(input, request)) {
////			throw new IllegalStateException(option + " Input supply " + input + " must meet or exceed requested supply" + request);
////		}
//		
//		// reduce our final option.  the original may have been overkill
//		// as long as we exhaust the input, no additional production capability
//		// would increase output.
////		EntityOption finalOption = entity.optionAboveInput(input);
//		ItemsFlow result = entity.isInput() ? requestedOutput : option.availableOutputLimited(requestedOutput, input);
//
//		System.out.println(option);
//		System.out.println("requested input: " + request);
//		System.out.println("available input: " + input);
//		System.out.println("produced output: " + result);
//		System.out.println();
//		
//		// calculate the output amount of the final option with the input
//		node.input = input;
//		node.output = result;
//		node.option = option;
//		return node;
//	}

    public SolveNode solveForOutput(Entity entity, final ItemsStack requestedOutput) {
        RatioNode ratio = solveRatio(entity);
        return solveRatioNode(ratio, requestedOutput);
    }

    private SolveNode solveRatioNode(RatioNode ratio, final ItemsStack requestedOutput) {
		Entity entity = ratio.entity;

        SolveNode node = new SolveNode();
		node.entity = entity;
		
//		// pick an output option that can satisfy the output
		List<EntityOption> options = entity.optionsAboveOutput(requestedOutput);
		ItemsStack[] requests = options.stream().map((e) -> e.requestedInputLimited(requestedOutput))
				.toArray(ItemsStack[]::new);
        // assume the player picks the best option
        // this way, the input belt will always be saturated
		ItemsStack request = ItemsStack.max(requests);

		ItemsStack requestRemaining = request;
		ItemsStack input = new ItemsStack();
		
		if(entity.isInput()) {
			input = request;
		} else {
			for(Entity source : entity.getInputs()) {
				SolveOption solveOption = new SolveOption();
				// recursively solve for the output of sources
                ItemsStack sourceRequest = request.filter(source.getOutputRatio().itemNames());
				SolveNode childNode = solveForOutput(source, request);
				ItemsStack[] outputs = childNode.options.stream()
						.map((e) -> e.output)
						.toArray(ItemsStack[]::new);

                // assume the player picks the worst option
                // subtract that from the request, and add to input
                // this way, the input belt will always be saturated
				ItemsStack minInput = ItemsStack.min(outputs);
				requestRemaining = ItemsStack.sub(requestRemaining, minInput);
				input = ItemsStack.add(input, minInput);
				
				node.sources.add(childNode);
			}
		}

		for(EntityOption option : options) {
			SolveOption solveOption = new SolveOption();
            solveOption.usageRatio = input.total() / option.maxInput();
			solveOption.input = input;
            solveOption.inputRequest = request;
			solveOption.output = option.availableOutputLimited(requestedOutput, input);
			solveOption.option = option;

            node.options.add(solveOption);
		}

        for(SolveOption option : node.options) {
            for(SolveOption compare : node.options) {
                boolean equalPlacement = option.option.placementCost() == compare.option.placementCost();
                boolean greaterCost = option.option.constructionCost() > compare.option.constructionCost();

                if(equalPlacement && greaterCost) {
                    option.unnecessary = true;
                    break;
                }
            }
        }

//		System.out.println(option);
//		System.out.println("requested input: " + request);
//		System.out.println("available input: " + input);
//		System.out.println("produced output: " + result);
//		System.out.println();
		


		
		// calculate the output amount of the final option with the input

		return node;
	}
//	private ItemsFlow getRequest(Entity entity, ItemsFlow requestedOutput) {
//		if(entity.getCrafter().isPresent()) {
//			Crafter craftItem = entity.getCrafter().get();
//			CrafterOption option = craftItem.optionAboveOutput(requestedOutput);
//			return option.requestedInputLimited(requestedOutput);
//		} else if (entity.getConnection().isPresent()) {
//			Crafter craftItem = entity.getCrafter().get();
//			CrafterOption option = craftItem.optionAboveOutput(requestedOutput);
//			return option.requestedInputLimited(requestedOutput);
//		} else {
//			throw new UnsupportedOperationException("Unknown entity type ")
//		}
//	}
//	
//	private ItemsFlow getOutput(Entity entity) {
//		if(entity.getCrafter().isPresent()) {
//			return getRequest(entity.getCrafter().get());
//		} else if (entity.getConnection().isPresent()) {
//			return getRequest(entity.getConnection().get());
//		} else {
//			throw new UnsupportedOperationException("Unknown entity type ")
//		}
//	}
	
//	public SolveNode solveForOutput(Crafter craftItem, ItemsFlow requestedOutput) {
//		SolveNode result = new SolveNode();
//		
//		ItemsFlow input = new ItemsFlow();
////		// pick an output option that can satisfy the output
//		CrafterOption option = craftItem.optionAboveOutput(requestedOutput);
////		
////		// ask the option how much input it needs.
//		ItemsFlow request = option.requestedInputLimited(requestedOutput);
//		for(Entity source : craftItem.getInputs()) {
//			// recursively solve for the output of sources
//			SolveNode node = solveForOutput(source, requestedOutput);
//			request = ItemsFlow.sub(request, node.output);
//			input = ItemsFlow.add(input, node.output);
//			result.sources.add(node);
//		}
//		
//		// reduce our final option.  the original may have been overkill
//		// as long as we exhaust the input, no additional production capability
//		// would increase output.
////		EntityOption finalOption = entity.optionAboveInput(input);
//
//		// calculate the output amount of the final option with the input
//		result.input = input;
//		result.output = craftItem.isInput() ? requestedOutput : option.outputFlow(input);
//		result.option = option;
//		return result;
//	}
//	
//	public SolveNode solveForOutput(Connection connection, ItemsFlow requestedOutput) {
//		SolveNode result = new SolveNode();
//		
//		ItemsFlow input = new ItemsFlow();
////		// pick an output option that can satisfy the output
//		ConnectionOption option = connection.optionAboveOutput(requestedOutput);
////		
////		// ask the option how much input it needs.
//		ItemsFlow request = requestedOutput;
//		for(Entity source : option.getInputs()) {
//			// recursively solve for the output of sources
//			SolveNode node = solveForOutput(source, requestedOutput);
//			request = ItemsFlow.sub(request, node.output);
//			input = ItemsFlow.add(input, node.output);
//			result.sources.add(node);
//		}
//		
//		// reduce our final option.  the original may have been overkill
//		// as long as we exhaust the input, no additional production capability
//		// would increase output.
////		EntityOption finalOption = entity.optionAboveInput(input);
//
//		// calculate the output amount of the final option with the input
//		result.input = input;
//		result.output = craftItem.isInput() ? requestedOutput : option.outputFlow(input);
//		result.option = option;
//		return result;
//	}
	
//	public double solveForInput(Entity entity) {
//		double worstRate = 0;
//		
//		for(Entity source : entity.getSources()) {
//			double sourceRate = solveForInput(source);
//			double rate = entity.getOutputRate(source, sourceRate);
//			if(rate > worstRate) {
//				worstRate = rate;
//			}
//		}
//		
//		return worstRate;
//	}
//	
//	public double solveForOutput(Entity entity, double entityRate) {
//		double worstRate = 0;
//		
//		for(Entity source : entity.getSources()) {
//			double sourceRate = entity.sourceRate(source, entityRate);
//			double optimalRate = entity.optimalRate(source, sourceRate);
//			double actualSourceRate = solveForOutput(source, optimalRate);
//			double rate = entity.getOutputRate(source, actualSourceRate);
//			if(rate > worstRate) {
//				worstRate = rate;
//			}
//		}
//		
//		return worstRate;
//	}
}

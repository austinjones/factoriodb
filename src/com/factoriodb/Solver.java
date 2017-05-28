package com.factoriodb;

import java.util.ArrayList;
import java.util.List;

import com.factoriodb.chain.Entity;
import com.factoriodb.chain.option.EntityOption;
import com.factoriodb.model.ItemsFlow;

public class Solver {
	private Model model;
	private Entity output;
	
	public Solver(Model model, Entity output) {
		this.model = model;
		this.output = output;
	}
	
	public static class SolveOption {
		public EntityOption option;
		public ItemsFlow input;
		public ItemsFlow output;
		
		@Override
		public String toString() {
			return option.toString() + " => " + output + " from " + input;
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
//		if (maxInput.anyMatch((m) -> m.flow() < maxInput.getDouble(m.item()))) {
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
//		// calculate the output flow of the final option with the input
//		node.input = input;
//		node.output = result;
//		node.option = option;
//		return node;
//	}
	
	public SolveNode solveForOutput(Entity entity, final ItemsFlow requestedOutput) {
		SolveNode node = new SolveNode();
		node.entity = entity;
		
//		// pick an output option that can satisfy the output
		List<EntityOption> options = entity.optionsAboveOutput(requestedOutput);
		ItemsFlow[] requests = options.stream().map((e) -> e.requestedInputLimited(requestedOutput))
				.toArray(ItemsFlow[]::new);
		ItemsFlow request = ItemsFlow.min(requests);
		
		ItemsFlow requestRemaining = request;
		ItemsFlow input = new ItemsFlow();
		
		if(entity.isInput()) {
			input = request;
		} else {
			for(Entity source : entity.getInputs()) {
				SolveOption solveOption = new SolveOption();
				// recursively solve for the output of sources
				SolveNode childNode = solveForOutput(source, request);
				ItemsFlow[] outputs = childNode.options.stream()
						.map((e) -> e.output)
						.toArray(ItemsFlow[]::new);
				
				ItemsFlow minOutput = ItemsFlow.min(outputs);
				requestRemaining = ItemsFlow.sub(requestRemaining, minOutput);
				input = ItemsFlow.add(input, minOutput);
				
				node.sources.add(childNode);
			}
		}
		
		
		for(EntityOption option : options) {
			SolveOption solveOption = new SolveOption();
			solveOption.input = input;
			solveOption.output = option.availableOutputLimited(requestedOutput, input);
			solveOption.option = option;
			node.options.add(solveOption);
		}

//		System.out.println(option);
//		System.out.println("requested input: " + request);
//		System.out.println("available input: " + input);
//		System.out.println("produced output: " + result);
//		System.out.println();
		


		
		// calculate the output flow of the final option with the input

		return node;
	}
//	private ItemsFlow getRequest(Entity entity, ItemsFlow requestedOutput) {
//		if(entity.getCrafter().isPresent()) {
//			Crafter crafter = entity.getCrafter().get();
//			CrafterOption option = crafter.optionAboveOutput(requestedOutput);
//			return option.requestedInputLimited(requestedOutput);
//		} else if (entity.getConnection().isPresent()) {
//			Crafter crafter = entity.getCrafter().get();
//			CrafterOption option = crafter.optionAboveOutput(requestedOutput);
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
	
//	public SolveNode solveForOutput(Crafter crafter, ItemsFlow requestedOutput) {
//		SolveNode result = new SolveNode();
//		
//		ItemsFlow input = new ItemsFlow();
////		// pick an output option that can satisfy the output
//		CrafterOption option = crafter.optionAboveOutput(requestedOutput);
////		
////		// ask the option how much input it needs.
//		ItemsFlow request = option.requestedInputLimited(requestedOutput);
//		for(Entity source : crafter.getInputs()) {
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
//		// calculate the output flow of the final option with the input
//		result.input = input;
//		result.output = crafter.isInput() ? requestedOutput : option.outputFlow(input);
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
//		// calculate the output flow of the final option with the input
//		result.input = input;
//		result.output = crafter.isInput() ? requestedOutput : option.outputFlow(input);
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
	
	public void solve() {
		// assemblers can be copied - scaled with input
		// inserters can be copied - scaled with speed of input/output
		// take input flow, scale to supply
	}
}

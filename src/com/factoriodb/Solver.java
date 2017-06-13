package com.factoriodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.factoriodb.chain.Entity;
import com.factoriodb.chain.option.EntityOption;
import com.factoriodb.model.ItemsStack;
import com.factoriodb.model.Model;

public class Solver {
	private Model model;
	
	public Solver(Model model) {
		this.model = model;
	}

    public static class SolveNode implements Iterable<SolveNode> {
        public Entity entity;
        public List<SolveNode> sources = new ArrayList<>();
        public List<SolveNode> targets = new ArrayList<>();

        public ItemsStack adjustedInput;
        public ItemsStack adjustedOutput;

        public List<SolveOption> options = new ArrayList<>();
        public ItemsStack request;
        public int recievedRequests = 0;

        @Override
        public Iterator<SolveNode> iterator() {
            return sources.iterator();
        }

        @Override
        public String toString() {
            return entity.toString();
        }
    }


    public void solveRatio(SolveNode node) {
        // TODO: fix double counting bug here, or some bug
        // seems to be throwing off the choices on advanced oil processing
        Entity entity = node.entity;

        if(node.entity.isInput()) {
            node.adjustedInput = entity.getInputRatio();
            node.adjustedOutput = entity.getOutputRatio();
            return;
        }

        ItemsStack input = new ItemsStack();
        for (SolveNode source : node.sources) {
            solveRatio(source);
            input = ItemsStack.add(input, source.adjustedOutput);
        }

        node.adjustedInput = input;
        node.adjustedOutput = entity.getOutputRatio(input);
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

    public SolveNode parse(Entity entity) {
        return parseRecurse(new HashMap<>(), entity);
    }

    private SolveNode parseRecurse(Map<Entity, SolveNode> map, Entity entity) {
        // deal with any structural issues here.  keep later logic contained to number crunching
        SolveNode node = map.get(entity);
        if(node != null) {
            return node;
        }

        node = new SolveNode();
        node.entity = entity;
        map.put(entity, node);

        if(entity.isInput()) {
            return node;
        }

        for(Entity source : entity.getInputs()) {
            SolveNode sourceNode = parseRecurse(map, source);
            if(!sourceNode.targets.contains(node)) {
                sourceNode.targets.add(node);
            }
            node.sources.add(sourceNode);
        }

        return node;
    }

    public SolveNode solveForOutput(Entity entity, final ItemsStack requestedOutput) {
        SolveNode node = parse(entity);
        solveRatio(node);
        solveRequest(node, requestedOutput);
        solveResponse(node);
        return node;
    }

    private void solveRequest(SolveNode node, ItemsStack requestedOutput) {
        Entity entity = node.entity;

        node.request = ItemsStack.add(node.request, requestedOutput);
        node.recievedRequests++;

        int targetSize = node.targets.size();
        if(targetSize > 0 && node.recievedRequests != node.targets.size()) {
            return;
        }

        ItemsStack finalRequestedOutput = node.request;

        //		// pick an output option that can satisfy the output
        List<EntityOption> options = entity.optionsAboveOutput(finalRequestedOutput);

        ItemsStack[] requests = options.stream().map((e) -> e.requestedInputLimited(finalRequestedOutput))
                .toArray(ItemsStack[]::new);

        // assume the player picks the best option
        // this way, the input belt will always be saturated
        ItemsStack request = ItemsStack.max(requests);
        node.request = request;

        for (SolveNode sourceNode : node.sources) {
            Entity source = sourceNode.entity;

//                System.out.println(entity + " <- " + source);
            ItemsStack adjusted = ItemsStack.div(sourceNode.adjustedOutput, node.adjustedInput);
            ItemsStack adjustedRequest = ItemsStack.mul(request, adjusted);
//                System.out.println(adjustedRequest);
            ItemsStack sourceRequest = adjustedRequest.filter(source.getOutputRatio().itemNames());

            solveRequest(sourceNode, sourceRequest);
        }

        node.options.clear();
        for(EntityOption option : options) {
            SolveOption solveOption = new SolveOption();
            solveOption.inputRequest = request;
            solveOption.option = option;

            node.options.add(solveOption);
        }
    }

    private void solveResponse(SolveNode node) {
        Entity entity = node.entity;

        // assume the player picks the best option
        // this way, the input belt will always be saturated
        ItemsStack request = node.request;

        ItemsStack input = new ItemsStack();

        if(entity.isInput()) {
            input = request;
        } else {
            for(SolveNode sourceNode : node.sources) {
                solveResponse(sourceNode);

                ItemsStack[] outputs = sourceNode.options.stream()
                        .map((e) -> e.output)
                        .toArray(ItemsStack[]::new);

                // pick the minimum
                ItemsStack minInput = ItemsStack.min(outputs);
                input = ItemsStack.add(input, minInput);
            }
        }


        for(SolveOption so : node.options) {
            EntityOption option = so.option;
            so.usageRatio = input.total() / option.maxInput();
            so.input = input;
            so.output = option.availableOutputLimited(node.request, input);
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
    }


}

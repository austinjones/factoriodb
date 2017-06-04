package com.factoriodb.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.factoriodb.chain.option.EntityOption;
import com.factoriodb.chain.option.ReplicatedOption;
import com.factoriodb.model.ItemsStack;


public abstract class Entity {
	protected List<Entity> inputs = new ArrayList<>();
	protected List<Entity> outputs = new ArrayList<>();
	
	protected boolean isInput;

    public abstract ItemsStack getOutputRatio();
    public abstract ItemsStack getOutputRatio(ItemsStack inputRatio);
    public abstract ItemsStack getInputRatio();
    public abstract Collection<? extends EntityOption> options();
	
	/**
	 * @return the option above (and closest to) the provided output assuming all inputs are satisfied
	 */
	public List<EntityOption> optionsAboveOutput(ItemsStack output) {
		List<EntityOption> options = new ArrayList<>();
		
		for(EntityOption opt : this.options()) {
			// TODO: bug here!  0s maybe shouldn't be counted? idk
			// belt filters items and procduces 0s
			ItemsStack flow = opt.availableOutputLimited(output);
			double ratio = output.maxratio(flow);
			int count = (int)Math.ceil(ratio);
			
			EntityOption replicated = new ReplicatedOption(opt, count);
			options.add(replicated);
		}
		
		return options;
	}
	
	/**
	 * @return the option above (and closest to) the provided output assuming all inputs are satisfied
	 */
	public EntityOption optionAboveOutput(ItemsStack output) {
		double bestRatio = 0;
		EntityOption bestOption = null;
		
		for(EntityOption opt : this.options()) {
			// TODO: bug here!  0s maybe shouldn't be counted? idk
			// belt filters items and procduces 0s
			ItemsStack flow = opt.availableOutputLimited(output);
			double ratio = flow.minratio(output);
			int count = (int)Math.ceil(1/ratio);
			
			EntityOption replicated = new ReplicatedOption(opt, count);
			flow = opt.availableOutputLimited(output);
			ratio = flow.minratio(output);
			if (bestRatio > 1 && ratio < bestRatio) {
				bestRatio = ratio;
				bestOption = replicated;
			} else if(bestRatio <= 1 && ratio > bestRatio) {
				bestRatio = ratio;
				bestOption = replicated;
			}
		}
		
		return bestOption;
	}

    public void connectFrom(Entity... inserterSources) {
        for(Entity source : inserterSources) {
            inputs.add(source);
        }
    }

	public void insertFrom(Entity... inserterSources) {
		for(Entity source : inserterSources) {
			Inserter input = new Inserter(source, this);
			inputs.add(input);
		}
	}
	
//	public List<Inserter> getInserterSources() {
//		return inputs;
//	}
	
	public List<Entity> getInputs() {
		return inputs;
	}
	
	public List<Entity> getOutputs() {
		return inputs;
	}

	public void markInput() {
		this.isInput = true;
	}
	
	public boolean isInput() {
		return isInput;
	}

	public Optional<Crafter> getCrafter() {
		return Optional.empty();
	}
	
	public Optional<Connection> getConnection() {
		return Optional.empty();
	}
	
//	/**
//	 * Returns the option above (and closest to) the input assuming all outputs are satisfied
//	 * @param input
//	 * @return
//	 */
//	public EntityOption optionAboveInput(final ItemsFlow input) {
//		ItemsFlow bestSurplus = null;
//		EntityOption bestOption = null;
//		
//		for(EntityOption opt : this.options()) {
//			ItemsFlow amount = opt.requestedInput();
//			ItemsFlow surplus = amount.sub(input, amount);
//			
//			if (surplus.anyMatch((r) -> r.amount() < 0)
//					&& surplus.totalIf((f) -> f > 0) < bestSurplus.totalIf((f) -> f > 0)) {
//				bestSurplus = surplus;
//				bestOption = opt;
//			}
//		}
//		
//		return bestOption;
//	}


}

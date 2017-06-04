package com.factoriodb.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.factoriodb.chain.option.ConnectionOption;
import com.factoriodb.chain.option.InserterOption;
import com.factoriodb.model.ItemsStack;

public class Inserter extends Connection {
	
	private Entity source;
	private Entity target;
	public Inserter(Entity source, Entity target) {
		super.inputs.add(source);
		super.outputs.add(target);
		
		this.source = source;
		this.target = target;
	}

	@Override
	public ItemsStack getOutputRatio() {
		return source.getOutputRatio();
	}

    @Override
    public ItemsStack getOutputRatio(ItemsStack inputRatio) {
        return source.getOutputRatio(inputRatio);
    }

    @Override
	public ItemsStack getInputRatio() {
		return target.getInputRatio();
	}
	
	public Entity getSource() {
		return source;
	}
	
	public Entity getTarget() {
		return target;
	}

	@Override
	public Collection<? extends ConnectionOption> options() {
		List<InserterOption> options = new ArrayList<>();
		options.add(new InserterOption(this, "inserter", 0.83, 1));
		options.add(new InserterOption(this, "long-handed-inserter", 1.15, 1));
		options.add(new InserterOption(this, "fast-inserter", 2.31, 1));
		options.add(new InserterOption(this, "stack-inserter", 2.31, 2));
		return options;
	}
	
	@Override
	public String toString() {
		return "inserter[" + target.getInputRatio() + "]";
	}
}

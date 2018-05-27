package com.factoriodb.chain.option;

import com.factoriodb.graph.RatedRecipe;

public class AssemblerOption extends RecipeOption {
	private static double SPEED_1 = 0.5;
	private double speed;
	
	public AssemblerOption(RatedRecipe recipe, String name, double speed) {
		super(recipe, name);
		this.speed = speed;
	}

    @Override
    public int count() {
        return (int)Math.ceil(this.ratedRecipe.getRate() / speed);
    }

    @Override
    public double input(String item) {
        return this.ratedRecipe.inputRate(item);
    }

    @Override
    public double output(String item) {
        return this.ratedRecipe.outputRate(item);
    }

    @Override
    public double maxInput(String item) {
        return count() * speed * this.getRecipe().outputRate(item);
    }

    @Override
    public double maxOutput(String item) {
        return count() * speed * this.getRecipe().outputRate(item);
    }
}

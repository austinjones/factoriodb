package com.factoriodb.chain.option;

import com.factoriodb.graph.RatedRecipe;
import com.factoriodb.graph.Recipe;

public abstract class RecipeOption extends EntityOption {
	private String optionDescription;
	protected RatedRecipe ratedRecipe;

	public RecipeOption(RatedRecipe recipe, String optionDescription) {
		this.ratedRecipe = recipe;
		this.optionDescription = optionDescription;
	}

    public String name() {
        return optionDescription;
    }

    public Recipe getRecipe() {
        return ratedRecipe.getRecipe();
    }

    public abstract int count();
    public abstract double input(String item);
    public abstract double output(String item);
    public abstract double maxInput(String item);
    public abstract double maxOutput(String item);

	public String toString() {
		return optionDescription;
	}
}

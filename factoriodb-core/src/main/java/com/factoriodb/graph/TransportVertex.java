package com.factoriodb.graph;

import com.factoriodb.chain.OptionFactory;
import com.factoriodb.chain.option.RecipeOption;

import java.util.Collection;

/**
 * @author austinjones
 */
public class TransportVertex {
    private ResourceVertex vertex;
    private Collection<? extends RecipeOption> solutions;

    public TransportVertex(ResourceVertex vertex) {
        this.vertex = vertex;
    }

    public RatedRecipe getRecipe() {
        return vertex.getRecipe();
    }

    public String name() {
        return vertex.getRecipe().getRecipe().name;
    }

    public double rate() {
        return vertex.getRate();
    }

    public Collection<? extends RecipeOption> getSolutions() {
        return solutions;
    }

    public TransportVertex updateSolutions(TransportGraph g) {
        solutions = OptionFactory.recipeOptions(getRecipe());
        return this;
    }

    @Override
    public String toString() {
        return vertex.toString();
    }
}

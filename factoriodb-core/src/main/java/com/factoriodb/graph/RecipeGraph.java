package com.factoriodb.graph;

import com.factoriodb.recipe.Recipe;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.HashSet;
import java.util.Set;

/**
 * @author austinjones
 */
public class RecipeGraph extends DefaultDirectedGraph<Recipe, RecipeEdge> {
    public RecipeGraph() {
        super(RecipeEdge.class);
    }

    public RecipeGraph(Class<? extends RecipeEdge> edgeClass) {
        super(edgeClass);
    }

    public RecipeGraph(EdgeFactory<Recipe, RecipeEdge> ef) {
        super(ef);
    }

    public void setEdgeResource(RecipeEdge e, String item) {
        assert (e instanceof RecipeEdge) : e.getClass();
        e.setItem(item);
    }

    public Set<String> satisfiedInputsOf(Recipe r) {
        Set<String> satisfiedInputs = new HashSet<>();
        for(RecipeEdge edge : this.edgesOf(r)) {
            Recipe source = this.getEdgeSource(edge);
            Recipe target = this.getEdgeTarget(edge);
            if(r.equals(target)) {
                satisfiedInputs.addAll(source.outputItems.keySet());
            }
        }

        return satisfiedInputs;
    }

    public Set<String> unsatisfiedInputsOf(Recipe r) {
        Set<String> inputs = new HashSet<>(r.inputItems.keySet());
        inputs.removeAll(satisfiedInputsOf(r));

        return inputs;
    }

    public Set<Recipe> recipesConsuming(String item) {
        Set<Recipe> recipes = new HashSet<>();
        for(Recipe r : this.vertexSet()) {
            if(r.inputItems.containsKey(item)) {
                recipes.add(r);
            }
        }

        return recipes;
    }

    public Set<Recipe> recipesSupplying(String item) {
        Set<Recipe> recipes = new HashSet<>();
        for(Recipe r : this.vertexSet()) {
            if(r.outputItems.containsKey(item)) {
                recipes.add(r);
            }
        }

        return recipes;
    }

    public Recipe getRecipe(String recipe) {
        for (Recipe r : this.vertexSet()) {
            if (recipe.equals(r.name)) {
                return r;
            }
        }

        return null;
    }
}

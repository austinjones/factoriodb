package com.factoriodb.recipe;

import com.factoriodb.recipe.RatedRecipe;
import com.factoriodb.model.CrafterType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author austinjones
 */
public class Recipe {
    public String name;
    public Map<String, Double> inputItems = new HashMap<>();
    public Map<String, Double> outputItems = new HashMap<>();
    public double time = 1;
    public CrafterType crafterType = CrafterType.ASSEMBLER;

    public Recipe() {

    }

    public Recipe(String item) {
        this.name = item;
        inputItems.put(item, 1.0);
        outputItems.put(item, 1.0);
        this.time = 0;
    }

    public RatedRecipe withRate(double rate) {
        return new RatedRecipe(this, rate);
    }

    public RatedRecipe withOutputRate(String item, double itemRate) {
        return null;
    }

    public double inputRatio(String input, Recipe source) {
        if (!inputItems.containsKey(input)) {
            throw new IllegalArgumentException("Unknown input " + input + " for recipe " + name);
        }

        if (!source.outputItems.containsKey(input)) {
            throw new IllegalArgumentException("Unknown input " + input + " for recipe " + name);
        }

        return source.outputRate(input) / this.inputRate(input);
    }

    public double ratio(String input, String output) {
        if (!inputItems.containsKey(input)) {
            throw new IllegalArgumentException("Unknown input " + input + " for recipe " + name);
        }

        if (!outputItems.containsKey(output)) {
            throw new IllegalArgumentException("Unknown input " + output + " for recipe " + name);
        }

        return this.inputRate(input) / this.outputRate(output);
    }

    public double outputRatio(String output, Recipe target) {
        if (!target.inputItems.containsKey(output)) {
            throw new IllegalArgumentException("Unknown input " + output + " for recipe " + name + " - target has inputs " + target.inputs() );
        }

        if (!outputItems.containsKey(output)) {
            throw new IllegalArgumentException("Unknown output " + output + " for recipe " + name + " - source has outputs " + this.outputs());
        }

        return this.outputRate(output) / target.inputRate(output);
    }

    public double outputRate(String item) {
        return 1.0 * outputItems.getOrDefault(item, 0.0) / time;
    }

    public double inputRate(String item) {
        return 1.0 * inputItems.getOrDefault(item, 0.0) / time;
    }

    public double totalOutput() {
        if (outputItems.isEmpty()) {
            return 0;
        }

        return outputItems.values()
                .stream()
                .mapToDouble((d) -> d)
                .sum();
    }

    public Set<String> inputs() {
        return inputItems.keySet();
    }

    public Set<String> outputs() {
        return outputItems.keySet();
    }

    @Override
    public String toString() {
        return name;
    }
}

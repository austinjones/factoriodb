package com.factoriodb.model;

/**
 * @author austinjones
 */
public class Recipe {
    private String name;
    private ItemsStack input;
    private ItemsStack output;
    private double energyRequired;
    private CrafterType crafterType;

    public Recipe(String name, ItemsStack input, ItemsStack output,
                  double energyRequired, CrafterType type) {
        this.name = name;
        this.input = input;
        this.output = output;
        this.energyRequired = energyRequired;
        this.crafterType = type;
    }

    public CrafterType getCrafterType() {
        return crafterType;
    }

    public ItemsStack getInput() {
        return input;
    }

    public ItemsStack getOutput() {
        return output;
    }

    public double getEnergyRequired() {
        return energyRequired;
    }

    public String name() {
        return name;
    }

    public String toString() {
        return name + " " + input + " => " + output;
    }
}

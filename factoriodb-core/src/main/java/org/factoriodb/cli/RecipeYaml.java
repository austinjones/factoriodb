package org.factoriodb.cli;

import java.util.HashMap;
import java.util.Map;

/**
 * @author austinjones
 */
public class RecipeYaml {
    private String name;
    private Double rate;
    private Map<String, RecipeItemYaml> input = new HashMap<>();
    private Map<String, RecipeItemYaml> output = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Map<String, RecipeItemYaml> getInput() {
        return input;
    }

    public void setInput(Map<String, RecipeItemYaml> input) {
        this.input = input;
    }

    public Map<String, RecipeItemYaml> getOutput() {
        return output;
    }

    public void setOutput(Map<String, RecipeItemYaml> output) {
        this.output = output;
    }

    public RecipeYaml() {}

    public RecipeYaml(String name) {
        this.name = name;
    }

    public RecipeYaml(String name, Double rate) {
        this.name = name;
        this.rate = rate;
    }
}

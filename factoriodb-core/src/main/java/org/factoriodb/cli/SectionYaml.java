package org.factoriodb.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author austinjones
 */
public class SectionYaml {
    private String name;
    private List<String> recipes = new ArrayList<>();
    private List<String> input = new ArrayList<>();
    private Map<String, Double> bind = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<String> recipes) {
        this.recipes = recipes;
    }

    public List<String> getInput() {
        return input;
    }

    public void setInput(List<String> input) {
        this.input = input;
    }

    public Map<String, Double> getBind() {
        return bind;
    }

    public void setBind(Map<String, Double> bind) {
        this.bind = bind;
    }
}

package org.factoriodb.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * @author austinjones
 */
public class RootYaml {
    private List<ConstraintYaml> outputs = new ArrayList<>();
    private List<ConstraintYaml> inputs = new ArrayList<>();
    private List<RecipeYaml> recipes = new ArrayList<>();

    public List<RecipeYaml> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<RecipeYaml> recipes) {
        this.recipes = recipes;
    }

    public List<ConstraintYaml> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<ConstraintYaml> outputs) {
        this.outputs = outputs;
    }

    public List<ConstraintYaml> getInputs() {
        return inputs;
    }

    public void setInputs(List<ConstraintYaml> inputs) {
        this.inputs = inputs;
    }
}

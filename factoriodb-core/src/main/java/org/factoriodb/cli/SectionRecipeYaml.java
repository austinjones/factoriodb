package org.factoriodb.cli;


public class SectionRecipeYaml {
    private String name;
    private Double constraint;

    public SectionRecipeYaml() {}

    public SectionRecipeYaml(String name) {
        this.name = name;
    }

    public SectionRecipeYaml(String name, Double constraint) {
        this.name = name;
        this.constraint = constraint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getConstraint() {
        return constraint;
    }

    public void setConstraint(Double constraint) {
        this.constraint = constraint;
    }
}

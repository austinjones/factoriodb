package org.factoriodb.cli;

import java.util.HashMap;
import java.util.Map;

/**
 * @author austinjones
 */
public class ConstraintYaml {
    private String item;
    private Double rate;

    public ConstraintYaml() {}

    public ConstraintYaml(String item) {
        this.item = item;
    }

    public ConstraintYaml(String item, Double rate) {
        this.item = item;
        this.rate = rate;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}

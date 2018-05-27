package com.factoriodb.graph;

import org.jgrapht.graph.DefaultEdge;

/**
 * @author austinjones
 */
public class RecipeEdge extends DefaultEdge {
    private String item;

    public RecipeEdge() {

    }

    protected void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    @Override
    public int hashCode() {
        return 13 * (item != null ? item.hashCode() : 0)
                + 13 * (getSource() != null ? getSource().hashCode() : 0)
                + 13 * (getTarget() != null ? getTarget().hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof RecipeEdge)) {
            return false;
        }

        RecipeEdge other = (RecipeEdge) obj;
        if (other.item == null && this.item != null) {
            return false;
        }

        return other.item.equals(this.item)
                && getSource().equals(other.getSource())
                && getTarget().equals(other.getTarget());
    }

    public String toString() {
        return item;
    }
}

package com.factoriodb.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author austinjones
 */
public class ResourceEdge extends DefaultWeightedEdge {
    private String item;

    public ResourceEdge() {

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
                + 13 * Double.hashCode(getWeight())
                + 13 * (getSource() != null ? getSource().hashCode() : 0)
                + 13 * (getTarget() != null ? getTarget().hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof ResourceEdge)) {
            return false;
        }

        ResourceEdge other = (ResourceEdge) obj;
        if (other.item == null && this.item != null) {
            return false;
        }

        return other.item.equals(this.item) && other.getWeight() == this.getWeight();
    }

    public String toString() {
        return item + " x " + this.getWeight();
    }
}

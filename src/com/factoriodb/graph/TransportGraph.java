package com.factoriodb.graph;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author austinjones
 */
public class TransportGraph extends DirectedWeightedMultigraph<TransportVertex, TransportEdge> {
    public TransportGraph() {
        super(TransportEdge.class);
    }

    /**
     * Assigns a weight to an edge.
     *
     * @param e      edge on which to set weight
     * @param weight new weight for edge
     * @see WeightedGraph#setEdgeWeight(Object, double)
     */
    public void setEdgeResource(TransportEdge e, String item, double weight) {
        assert (e instanceof ResourceEdge) : e.getClass();
        e.setItem(item);
        this.setEdgeWeight(e, weight);
    }

    public Set<TransportEdge> targetsOf(TransportVertex vertex) {
        Set<TransportEdge> edges = new HashSet<>();
        for (TransportEdge e : this.edgesOf(vertex)) {
            TransportVertex source = this.getEdgeSource(e);
            if (source != null && source.equals(vertex)) {
                edges.add(e);
            }
        }

        return edges;
    }

    public Collection<TransportEdge> sourcesOf(TransportVertex vertex) {
        Set<TransportEdge> edges = new HashSet<>();
        for (TransportEdge e : this.edgesOf(vertex)) {
            TransportVertex target = this.getEdgeTarget(e);
            if (target != null && target.equals(vertex)) {
                edges.add(e);
            }
        }

        return edges;
    }

    public TransportVertex getVertex(String recipe) {
        for (TransportVertex v : this.vertexSet()) {
            if (recipe.equals(v.getRecipe().getRecipe().name)) {
                return v;
            }
        }

        return null;
    }

    public TransportVertex getVertex(Recipe r) {
        if (r == null) {
            return null;
        }

        return getVertex(r.name);
    }
}


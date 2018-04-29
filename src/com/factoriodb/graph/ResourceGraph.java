package com.factoriodb.graph;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author austinjones
 */
public class ResourceGraph extends DirectedWeightedMultigraph<ResourceVertex, ResourceEdge> {
    public ResourceGraph() {
        super(ResourceEdge.class);
    }

    /**
     * Assigns a weight to an edge.
     *
     * @param e      edge on which to set weight
     * @param weight new weight for edge
     * @see WeightedGraph#setEdgeWeight(Object, double)
     */
    public void setEdgeResource(ResourceEdge e, String item, double weight) {
        assert (e instanceof ResourceEdge) : e.getClass();
        e.setItem(item);
        this.setEdgeWeight(e, weight);
    }

    public void addResourceEdge(ResourceVertex source, ResourceVertex target, String item, double weight) {
        ResourceEdge edge = this.addEdge(source, target);
        setEdgeResource(edge, item, weight);
    }

    public Set<ResourceEdge> targetsOf(ResourceVertex vertex) {
        Set<ResourceEdge> edges = new HashSet<>();
        for (ResourceEdge e : this.edgesOf(vertex)) {
            ResourceVertex source = this.getEdgeSource(e);
            if (source != null && source.equals(vertex)) {
                edges.add(e);
            }
        }

        return edges;
    }

    public Collection<ResourceEdge> sourcesOf(ResourceVertex vertex) {
        Set<ResourceEdge> edges = new HashSet<>();
        for (ResourceEdge e : this.edgesOf(vertex)) {
            ResourceVertex target = this.getEdgeTarget(e);
            if (target != null && target.equals(vertex)) {
                edges.add(e);
            }
        }

        return edges;
    }

    public double inputRate(ResourceVertex vertex, String item) {
        return this.sourcesOf(vertex).stream()
                .filter((e)->item.equals(e.getItem()))
                .mapToDouble((e) -> this.getEdgeWeight(e))
                .sum();
    }

    public double outputRate(ResourceVertex vertex, String item) {
        return this.targetsOf(vertex).stream()
                .filter((e)->item.equals(e.getItem()))
                .mapToDouble((e) -> this.getEdgeWeight(e))
                .sum();

    }


    public void scaleVertex(ResourceVertex vertex, double factor) {
        vertex.setRate(vertex.getRate() * factor);
    }

    public void scaleEdge(ResourceEdge edge, double factor) {
        this.setEdgeWeight(edge, this.getEdgeWeight(edge) * factor);
    }

    public <T> void rescale(double factor) {
        for (ResourceEdge edge : this.edgeSet()) {
            this.scaleEdge(edge, factor);
        }

        for (ResourceVertex vertex : this.vertexSet()) {
            this.scaleVertex(vertex, factor);
        }
    }

    public Map<String, Double> inputsOf(ResourceVertex vertex) {
        return this.sourcesOf(vertex).stream().collect(
                Collectors.groupingBy(e->e.getItem(), Collectors.summingDouble(e->this.getEdgeWeight(e))));
    }

    public Map<String, Double> outputsOf(ResourceVertex vertex) {
        return this.targetsOf(vertex).stream().collect(
                Collectors.groupingBy(e->e.getItem(), Collectors.summingDouble(e->this.getEdgeWeight(e))));
    }
}

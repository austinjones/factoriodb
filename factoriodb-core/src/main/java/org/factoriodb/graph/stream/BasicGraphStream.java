package org.factoriodb.graph.stream;

import org.factoriodb.graph.EdgeMapper;
import org.factoriodb.graph.StablizationException;
import org.factoriodb.graph.VertexMapper;
import org.jgrapht.Graph;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author austinjones
 */
public class BasicGraphStream<V, E, G extends Graph<V,E>> implements GraphStream<V,E> {
    private G graph;

    public BasicGraphStream(G graph) {
        this.graph = graph;
    }

    public EdgeStream<E> edges() {
        return new BasicEdgeStream(graph.edgeSet());
    }

    @Override
    public VertexStream<V> verteces() {
        return new BasicVertexStream(graph.vertexSet());
    }

    public EdgeStream edges(Predicate<E> where) {
        return null;
    }

    public <V2, E2, G extends Graph<V2, E2>> G map(
            G to,
            VertexMapper<G, V, V2> convertVertex,
            EdgeMapper<G, E, E2> convertEdge
    ) {
        Map<V, V2> vertexMap = new HashMap<>();
        for(V v1 : graph.vertexSet()) {
            V2 v2 = convertVertex.convert(to, v1);
            if(v2 == null) {
                throw new NullPointerException(v1 + " was converted to null.");
            }
            vertexMap.put(v1, v2);
            to.addVertex(v2);
        }

        for(E e1 : graph.edgeSet()) {
            V2 sourceV2 = vertexMap.get(graph.getEdgeSource(e1));
            V2 targetV2 = vertexMap.get(graph.getEdgeTarget(e1));
            E2 e2 = convertEdge.convert(to, e1);
            to.addEdge(sourceV2, targetV2, e2);
        }

        return to;
    }

    /**
     * Warning: relies on correct implementations of Vertex and Edge equals and hashCode methods.
     * @param targetGenerator
     * @param convertVertex
     * @param convertEdge
     * @return
     */
    public G mapUntilStable(
            Supplier<G> targetGenerator,
            VertexMapper<G, V, V> convertVertex,
            EdgeMapper<G, E, E> convertEdge
    ) {
        // the behavior of this function is a little scary.
        // but I think it's worth it for the downstream code simplification
        // the Graph.equals() method that is commonly used (AbstractGraph)
        // relies on hashCode() and equals(Object) implementations of vertex/edge classes.
        // if we can't stabilize the output within 10,000 iterations.  we abort with a RuntimeException.
        int iteration = 0;

        Graph<V,E> last = null;
        G current = graph;

        System.out.println("Source: " + current);
        while(last == null || !last.equals(current)) {
            G to = targetGenerator.get();

            Map<V,V> vertexMap = new HashMap<>();
            for(V v1 : current.vertexSet()) {
                V v2 = convertVertex.convert(current, v1);
                vertexMap.put(v1, v2);
                to.addVertex(v2);
            }

            for(E e1 : current.edgeSet()) {
                V sourceV2 = vertexMap.get(current.getEdgeSource(e1));
                V targetV2 = vertexMap.get(current.getEdgeTarget(e1));
                E e2 = convertEdge.convert(current, e1);
                boolean added = to.addEdge(sourceV2, targetV2, e2);
                if(!added) {
                    throw new IllegalStateException("Failed to add edge " + e2 + " from " + sourceV2+ " to " + targetV2);
                }
            }

            last = current;
            current = to;
            iteration++;
            System.out.println("Iteration " + iteration + ": " + current);
            if(iteration > 10000) {
                throw new StablizationException("Didn't stablize: " + current);
            }
        }

        return current;
    }
}

package org.factoriodb.graph.stream;

/**
 * @author austinjones
 */
public interface GraphStream<V,E> {
    public EdgeStream<E> edges();
    public VertexStream<V> verteces();
}

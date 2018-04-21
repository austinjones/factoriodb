package com.factoriodb.graph;

/**
 * @author austinjones
 */
public interface VertexMapper<G, V1, V2> {
    public V2 convert(G g, V1 v1);
}

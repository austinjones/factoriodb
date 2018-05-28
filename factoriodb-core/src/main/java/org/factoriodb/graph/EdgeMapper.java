package org.factoriodb.graph;

/**
 * @author austinjones
 */
public interface EdgeMapper<G, E1, E2> {
    public E2 convert(G g, E1 e1);
}

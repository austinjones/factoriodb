package org.factoriodb.graph;

/**
 * @author austinjones
 */
public class GraphMapper<G1, V1, E1, G2, V2, E2> {
    private G1 from;
    private G2 to;

    public GraphMapper(G1 from, G2 to) {
        this.from = from;
        this.to = to;
    }


}

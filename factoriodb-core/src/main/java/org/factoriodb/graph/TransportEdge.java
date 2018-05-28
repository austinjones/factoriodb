package org.factoriodb.graph;

import org.factoriodb.chain.OptionFactory;
import org.factoriodb.chain.option.ConnectionOption;

import java.util.Collection;

/**
 * @author austinjones
 */
public class TransportEdge extends ResourceEdge {
    private Collection<? extends ConnectionOption> solutions;
    public TransportEdge() {

    }

    public Collection<? extends ConnectionOption> getSolutions() {
        return solutions;
    }

    public TransportEdge updateSolutions(TransportGraph graph) {
        double rate = graph.getEdgeWeight(this);
        solutions = OptionFactory.connectionOptions(this.getItem(), rate);
        return this;
    }
}


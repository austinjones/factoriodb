package org.factoriodb.solution;

import org.factoriodb.graph.TransportEdge;
import org.factoriodb.graph.TransportGraph;
import org.factoriodb.graph.TransportVertex;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.List;

import static jdk.nashorn.internal.objects.Global.print;

public class Solution {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TransportGraph graph;
        private String assemblerPreference = "assembling-machine";
        private String beltPreference = "transport-belt";
        private String furnacePreference = "stone-furnace";

        public Builder from(TransportGraph transport) {
            this.graph = transport;
            return this;
        }

        public Builder withAssemblerPreference(String preference) {
            this.assemblerPreference = preference;
            return this;
        }

        public Builder withBeltPreference(String preference) {
            this.furnacePreference = preference;
            return this;
        }

        public Builder withFurnacePreference(String preference) {
            this.beltPreference = preference;
            return this;
        }

        private static Solution.Element buildSolutionElement(TransportGraph g, TransportVertex v) {
            // for each input:

            return null;
        }

        public Solution build() {
            List<Element> solutions = new ArrayList<>();

            TopologicalOrderIterator<TransportVertex, TransportEdge> iter = new TopologicalOrderIterator(graph);
            while (iter.hasNext()) {
                TransportVertex v = iter.next();
                Solution.Element element = buildSolutionElement(graph, v);
            }

            return new Solution();
        }
    }

    public static class Element {

    }
}

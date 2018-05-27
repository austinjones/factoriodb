package com.factoriodb.network;

import java.util.List;

public class NetworkVertex<T> {
	private T vertex;
	private List<NetworkVertex<T>> edges;
	
	public NetworkVertex(T vertex, List<NetworkVertex<T>> edges) {
		this.vertex = vertex;
		this.edges = edges;
	}
	
	public T getVertex() {
		return vertex;
	}
	
	public List<NetworkVertex<T>> getEdges() {
		return edges;
	}
}

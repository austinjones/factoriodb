package com.factoriodb.network;

public class NetworkEdge<T> {
	private T edge;
	private NetworkVertex<T> vertex;
	
	public NetworkEdge(T edge, NetworkVertex<T> vertex) {
		this.edge = edge;
		this.vertex = vertex;
	}
	
	
}

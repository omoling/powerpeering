package it.unibz.inf.cn.util;

public class QueryHit {

	
	private String peer;
	private String resource;
	
	public QueryHit(String peer, String resource) {
		this.peer = peer;
		this.resource = resource;
	}
	
	public String getPeer() {
		return peer;
	}
	
	public String getResource() {
		return resource;
	}
}

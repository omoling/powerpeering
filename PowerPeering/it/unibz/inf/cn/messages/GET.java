package it.unibz.inf.cn.messages;

public class GET extends PPMessage {

	public String resource;
	
	public GET(String from, String to) {
		super(from, to);
	}
	
	public void setResource(String resource) {
		this.resource = resource;
	}
	
	public String getResource() {
		return resource;
	}

	@Override
	public String getId() {
		return PPMessage.GET_ID;
	}
	
	public String toString() {
		return super.toString() + " RESOURCE: " + getResource();
	}

}

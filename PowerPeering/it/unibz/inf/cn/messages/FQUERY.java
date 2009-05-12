package it.unibz.inf.cn.messages;

public class FQUERY extends QUERY {
	
	private String requestor;

	public FQUERY(String from, String to) {
		super(from, to);
	}
	
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
	
	public String getRequestor() {
		return requestor;
	}

	@Override
	public String getId() {
		return PPMessage.FQUERY_ID;
	}
	
	public String toString() {
		return super.toString() + " REQUESTOR: " + getRequestor();
	}

}

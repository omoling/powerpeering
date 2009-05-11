package it.unibz.inf.cn.messages;

public class FPING extends PING {
	
	private String requestor;
	
	public FPING(String from, String to) {
		super(from, to);
	}

	@Override
	public String getId() {
		return PPMessage.FPING_ID;
	}
	
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
	
	public String getRequestor() {
		return requestor;
	}
}

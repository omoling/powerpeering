package it.unibz.inf.cn.messages;

public abstract class PPMessage {
	
	public static final String PING_ID = "PING";
	public static final String FPING_ID = "FPING";
	public static final String PONG_ID = "PONG";
	
	private String from;
	private String to;
	
	protected PPMessage(String from, String to) {
		this.from = from;
		this.to = to;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;
	}
	
	public abstract String getId();
	
	public String toString() {
		return getId() + " FROM: <" + getFrom() + "> TO: <" + getTo() + ">";
	}
}

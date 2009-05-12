package it.unibz.inf.cn.messages;

public class PING extends PPMessage {
	
	private int ttl;
	
	public PING(String from, String to) {
		super(from, to);
	}
	
	public void setTTL(int ttl) {
		this.ttl = ttl;
	}
	
	public int getTTL() {
		return ttl;
	}

	@Override
	public String getId() {
		return PPMessage.PING_ID;
	}
	
	public String toString() {
		return super.toString() + " TTL: " + getTTL();
	}
}

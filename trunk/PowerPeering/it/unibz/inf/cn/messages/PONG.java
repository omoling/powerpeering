package it.unibz.inf.cn.messages;

public class PONG extends PPMessage {

	public PONG(String from, String to) {
		super(from, to);
	}

	@Override
	public String getId() {
		return PPMessage.PONG_ID;
	}
}

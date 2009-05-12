package it.unibz.inf.cn.messages;

import java.io.File;

public class POST extends PPMessage {
	
	private File attachment;

	public POST(String from, String to) {
		super(from, to);
	}
	
	public void setAttachment(File attachment) {
		this.attachment = attachment;
	}
	
	public File getAttachment() {
		return attachment;
	}

	@Override
	public String getId() {
		return PPMessage.POST_ID;
	}
	
	public String toString() {
		return super.toString() + " ATTACHMENT: " + getAttachment().getName();
	}
}

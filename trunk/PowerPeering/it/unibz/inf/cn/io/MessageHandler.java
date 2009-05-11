package it.unibz.inf.cn.io;

import javax.mail.Message;

public interface MessageHandler {
	
	public boolean handleMessage(Message m);

}

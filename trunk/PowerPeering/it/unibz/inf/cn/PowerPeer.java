package it.unibz.inf.cn;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import it.unibz.inf.cn.io.MessageHandler;
import it.unibz.inf.cn.io.POP3;
import it.unibz.inf.cn.io.PPMessages;
import it.unibz.inf.cn.messages.PPMessage;

public class PowerPeer implements MessageHandler {

	
	public static void main(String[] args) throws AddressException, MessagingException, IOException {
		
		String serverHost = "localhost";
		String from = "from@fromdomain.com";
		String to = "default@cn.com";
		
		PPMessages.sendPING(serverHost, from, to, 3);
		PPMessages.sendFPING(serverHost, from, to, from, 3);
		PPMessages.sendPONG(serverHost, from, to);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		POP3.pop(serverHost, "default", "pwd", new PowerPeer());
	}
	
	public static String getHost() {
		return "localhost";
	}
	
	public static String getEmail() {
		return "default@cn.com";
	}

	@Override
	public boolean handleMessage(Message m) {
		PPMessage message = PPMessages.parseMessage(m);
		if(message == null) {
			return false;
		}
		
		System.out.println(message);
		
		
		return true;
	}
	
}

package it.unibz.inf.cn;

import java.io.File;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import it.unibz.inf.cn.io.MessageHandler;
import it.unibz.inf.cn.io.POP3;
import it.unibz.inf.cn.io.PPMessages;
import it.unibz.inf.cn.messages.*;

public abstract class PowerPeer implements MessageHandler {

	private String serverHost;
	private String email;
	private String user;
	private String pwd;
	private int ttl;
	private boolean isRunning;
	
	
	public PowerPeer(String serverHost, String email, String user, String pwd, int ttl) {
		this.serverHost = serverHost;
		this.email = email;
		this.user = user;
		this.pwd = pwd;
		this.ttl = ttl;
		isRunning = true;
	}
	
	public void handleMessage(PPMessage message) {
		if(message instanceof FQUERY)
			handleMessage((FQUERY) message);
		else if(message instanceof QUERY)
			handleMessage((QUERY) message);
		else if(message instanceof FPING)
			handleMessage((FPING) message);
		else if(message instanceof PING)
			handleMessage((PING) message);
		else if(message instanceof PONG)
			handleMessage((PONG) message);
		else if(message instanceof QUERYHIT)
			handleMessage((QUERYHIT) message);
		else if(message instanceof GET)
			handleMessage((GET) message);
		else if(message instanceof POST)
			handleMessage((POST) message);
		else
			throw new RuntimeException("Unrecognized message " + message.getClass());
	}
	
	protected abstract void handleMessage(PING message);
	
	protected abstract void handleMessage(FPING message);
	
	protected abstract void handleMessage(PONG message);
	
	protected abstract void handleMessage(QUERY message);
	
	protected abstract void handleMessage(FQUERY message);
	
	protected abstract void handleMessage(QUERYHIT message);
	
	protected abstract void handleMessage(GET message);
	
	protected abstract void handleMessage(POST message);
	
	protected void startListener(final int sec) {
		final PowerPeer peer = this;
		new Thread() {
			public void run() {
				while(peer.isRunning) {
					try {
						POP3.pop(peer.serverHost, peer.user, peer.pwd, peer);
						Thread.sleep(sec * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	protected void stopListener() {
		isRunning = false;
	}
	
	public void sendPING(String to) throws AddressException, MessagingException {
		PPMessages.sendPING(serverHost, email, to, ttl);
	}
	
	protected void sendFPING(String to, String requestor, int ttl) throws AddressException, MessagingException {
		PPMessages.sendFPING(serverHost, email, to, requestor, ttl);
	}
	
	protected void sendPONG(String to) throws AddressException, MessagingException {
		PPMessages.sendPONG(serverHost, email, to);
	}
	
	public void sendQUERY(String to, String expression) throws AddressException, MessagingException {
		PPMessages.sendQUERY(serverHost, email, to, expression, ttl);
	}
	
	protected void sendFQUERY(String to, String requestor, String expression, int ttl) throws AddressException, MessagingException {
		PPMessages.sendFQUERY(serverHost, email, to, expression, requestor, ttl);
	}
	
	protected void sendQUERYHIT(String to, String expression, List<String> resources) throws AddressException, MessagingException {
		PPMessages.sendQUERYHIT(serverHost, email, to, expression, resources);
	}
	
	public void sendGET(String to, String resource) throws AddressException, MessagingException {
		PPMessages.sendGET(serverHost, email, to, resource);
	}
	
	protected void sendPOST(String to, File resource) throws AddressException, MessagingException {
		PPMessages.sendPOST(serverHost, email, to, resource);
	}

	@Override
	public boolean handleMessage(Message m) {
		PPMessage message = PPMessages.parseMessage(m);
		if(message == null) {
			return false;
		}
		handleMessage(message);
		return true;
	}
	
	public String getUser() {
		return user;
	}
}

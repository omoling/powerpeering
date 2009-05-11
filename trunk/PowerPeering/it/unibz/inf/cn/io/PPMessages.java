package it.unibz.inf.cn.io;

import it.unibz.inf.cn.messages.FPING;
import it.unibz.inf.cn.messages.PING;
import it.unibz.inf.cn.messages.PONG;
import it.unibz.inf.cn.messages.PPMessage;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeBodyPart;

public class PPMessages {
	
	public static void sendPING(String serverHost, String from, String to, int ttl) throws AddressException, MessagingException {
		String subject = "PING";
		String body = "TTL: " + ttl;
		SMTP.send(serverHost, from, to, subject, body);
	}
	
	public static void sendFPING(String serverHost, String from, String to, String requestor, int ttl) throws AddressException, MessagingException {
		String subject = "FPING";
		String body = 	"TTL: " + ttl + "\r\n" +
						"REQUESTOR: " + requestor;
		SMTP.send(serverHost, from, to, subject, body);
	}
	
	public static void sendPONG(String serverHost, String from, String to) throws AddressException, MessagingException {
		String subject = "PONG";
		SMTP.send(serverHost, from, to, subject);
	}
	
	public static PPMessage parseMessage(Message m) {
		String[] header;
		try {
			// check if right header
			header = m.getHeader("X-Mailer");
			if(header.length != 1 || !header[0].equals("PowerPeering"))
				return null;
			
			if(m.getSubject().equals(PPMessage.PING_ID)) {
				String from = m.getFrom()[0].toString();
				String to = m.getRecipients(RecipientType.TO)[0].toString();
				PING ping = new PING(from, to);
				
				Multipart multipart = (Multipart) m.getContent();
				MimeBodyPart textPart = (MimeBodyPart) multipart.getBodyPart(0);
				
				String text = (String) textPart.getContent();
				
				ping.setTTL(Integer.parseInt(text.substring(text.indexOf(" ")+1, text.length())));
				
				return ping;
			}
			
			if(m.getSubject().equals(PPMessage.FPING_ID)) {
				String from = m.getFrom()[0].toString();
				String to = m.getRecipients(RecipientType.TO)[0].toString();
				FPING fping = new FPING(from, to);
				
				Multipart multipart = (Multipart) m.getContent();
				MimeBodyPart textPart = (MimeBodyPart) multipart.getBodyPart(0);
				
				String text = (String) textPart.getContent();
				
				String[] lines = text.split("\r\n");
				
				fping.setTTL(Integer.parseInt(lines[0].substring(lines[0].indexOf(" ")+1, lines[0].length())));
				fping.setRequestor(lines[1].substring(lines[1].indexOf(" ")+1, lines[1].length()));
				
				return fping;
			}
			
			if(m.getSubject().equals(PPMessage.PONG_ID)) {
				String from = m.getFrom()[0].toString();
				String to = m.getRecipients(RecipientType.TO)[0].toString();
				PONG pong = new PONG(from, to);

				return pong;
			}
		} catch (Throwable t) {
			System.err.println(t); // TODO change !!!
		}
		return null; // error in parsing 
	}
}

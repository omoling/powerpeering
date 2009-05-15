package it.unibz.inf.cn.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import it.unibz.inf.cn.messages.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeBodyPart;

public class PPMessages {
	
	public static void sendPING(String serverHost, String from, String to, int ttl) throws AddressException, MessagingException {
		String subject = PPMessage.PING_ID;
		String body = "TTL: " + ttl;
		SMTP.send(serverHost, from, to, subject, body);
	}
	
	public static void sendFPING(String serverHost, String from, String to, String requestor, int ttl) throws AddressException, MessagingException {
		String subject = PPMessage.FPING_ID;
		String body = 	"TTL: " + ttl + "\r\n" +
						"REQUESTOR: " + requestor;
		SMTP.send(serverHost, from, to, subject, body);
	}
	
	public static void sendPONG(String serverHost, String from, String to) throws AddressException, MessagingException {
		String subject = PPMessage.PONG_ID;
		SMTP.send(serverHost, from, to, subject);
	}
	
	public static void sendGET(String serverHost, String from, String to, String resource) throws AddressException, MessagingException {
		String subject = PPMessage.GET_ID;
		String body = "RESOURCE: " + resource;
		SMTP.send(serverHost, from, to, subject, body); 
	}
	
	public static void sendPOST(String serverHost, String from, String to, File resource) throws AddressException, MessagingException {
		String subject = PPMessage.POST_ID;
		SMTP.send(serverHost, from, to, subject, resource); 
	}
	
	public static void sendQUERY(String serverHost, String from, String to, String expression, int ttl) throws AddressException, MessagingException {
		String subject = PPMessage.QUERY_ID;
		String body = 	"TTL: " + ttl + "\r\n" +
						"EXPRESSION: " + expression;
		SMTP.send(serverHost, from, to, subject, body);
	}
	
	public static void sendFQUERY(String serverHost, String from, String to, String expression, String requestor, int ttl) throws AddressException, MessagingException {
		String subject = PPMessage.FQUERY_ID;
		String body = 	"TTL: " + ttl + "\r\n" +
						"EXPRESSION: " + expression + "\r\n" + 
						"REQUESTOR: " + requestor;
		SMTP.send(serverHost, from, to, subject, body);
	}
	
	public static void sendQUERYHIT(String serverHost, String from, String to, String expression, List<String> resources) throws AddressException, MessagingException {
		String subject = PPMessage.QUERYHIT_ID;
		StringBuffer body = new StringBuffer();
		body.append("EXPRESSION: " + expression + "\r\n");
		body.append("RESOURCES: ");	
		for(int i = 0; i < resources.size(); i++) {	
			if(i != 0)
				body.append("|");
			body.append(resources.get(i));
		}
		SMTP.send(serverHost, from, to, subject, body.toString());
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
			
			if(m.getSubject().equals(PPMessage.GET_ID)) {
				String from = m.getFrom()[0].toString();
				String to = m.getRecipients(RecipientType.TO)[0].toString();
				
				GET get = new GET(from, to);
				
				Multipart multipart = (Multipart) m.getContent();
				MimeBodyPart textPart = (MimeBodyPart) multipart.getBodyPart(0);
				
				String text = (String) textPart.getContent();
				
				get.setResource(text.substring(text.indexOf(" ")+1, text.length()));
				
				return get;
			}
			
			if(m.getSubject().equals(PPMessage.POST_ID)) {
				
				String from = m.getFrom()[0].toString();
				String to = m.getRecipients(RecipientType.TO)[0].toString();
				
				POST post = new POST(from, to);
				
				Multipart multipart = (Multipart) m.getContent();
				MimeBodyPart attachmentPart = (MimeBodyPart) multipart.getBodyPart(0);
				String filename = attachmentPart.getFileName();
				InputStream istream = attachmentPart.getDataHandler().getDataSource().getInputStream();
				File tmp = new File("jumbo/resources/" + filename); // TODO better path handling
				OutputStream fout = new FileOutputStream(tmp);
				
				byte[] buff = new byte[10];
				int len;
				while((len = istream.read(buff)) != -1) {
					fout.write(buff, 0, len);
				}
				fout.close();		
				post.setAttachment(tmp);
				return post;
			}
			
			if(m.getSubject().equals(PPMessage.QUERY_ID)) {
				String from = m.getFrom()[0].toString();
				String to = m.getRecipients(RecipientType.TO)[0].toString();
				QUERY query = new QUERY(from, to);
				
				Multipart multipart = (Multipart) m.getContent();
				MimeBodyPart textPart = (MimeBodyPart) multipart.getBodyPart(0);
				
				String text = (String) textPart.getContent();

				String[] lines = text.split("\r\n");
				
				query.setTTL(Integer.parseInt(lines[0].substring(lines[0].indexOf(" ")+1, lines[0].length())));
				query.setExpression(lines[1].substring(lines[1].indexOf(" ")+1, lines[1].length()));
				
				return query;
			}
			
			if(m.getSubject().equals(PPMessage.FQUERY_ID)) {
				String from = m.getFrom()[0].toString();
				String to = m.getRecipients(RecipientType.TO)[0].toString();
				FQUERY fquery = new FQUERY(from, to);
				
				Multipart multipart = (Multipart) m.getContent();
				MimeBodyPart textPart = (MimeBodyPart) multipart.getBodyPart(0);
				
				String text = (String) textPart.getContent();

				String[] lines = text.split("\r\n");
				
				fquery.setTTL(Integer.parseInt(lines[0].substring(lines[0].indexOf(" ")+1, lines[0].length())));
				fquery.setExpression(lines[1].substring(lines[1].indexOf(" ")+1, lines[1].length()));
				fquery.setRequestor(lines[2].substring(lines[2].indexOf(" ")+1, lines[2].length()));
				
				return fquery;
			}
			
			if(m.getSubject().equals(PPMessage.QUERYHIT_ID)) {
				String from = m.getFrom()[0].toString();
				String to = m.getRecipients(RecipientType.TO)[0].toString();
				QUERYHIT queryhit = new QUERYHIT(from, to);
				
				Multipart multipart = (Multipart) m.getContent();
				MimeBodyPart textPart = (MimeBodyPart) multipart.getBodyPart(0);
				
				String text = (String) textPart.getContent();
				
				String[] lines = text.split("\r\n");
				
				String expression = lines[0].substring(lines[0].indexOf(" ")+1, lines[0].length());
				
				queryhit.setExpression(expression);
				
				String resourceList = lines[1].substring(lines[1].indexOf(" ")+1, lines[1].length());
				
				String[] resources = resourceList.split("\\|");
				
				for(String resource : resources)
					queryhit.addResource(resource);
				
				return queryhit;
			}
			
			
			
		} catch (Throwable t) {
			System.err.println(t); // TODO change !!!
		}
		return null; // error in parsing 
	}
}

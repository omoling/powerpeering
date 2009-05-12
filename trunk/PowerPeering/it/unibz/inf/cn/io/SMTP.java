package it.unibz.inf.cn.io;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

public class SMTP {
	
	public static void send(String serverHost, String from, String to,
			String subject, String bodyText)
			throws AddressException, MessagingException {
		
		send(serverHost, from, to, subject, bodyText, null);
	}
	
	public static void send(String serverHost, String from, String to,
			String subject)
			throws AddressException, MessagingException {
		
		send(serverHost, from, to, subject, null, null);
	}
	
	public static void send(String serverHost, String from, String to,
			String subject, File attachment)
			throws AddressException, MessagingException {
		send(serverHost, from, to, subject, null, attachment);
	}
	
	public static void send(String serverHost, String from, String to,
			String subject, String bodyText, File attachment)
			throws AddressException, MessagingException {

		Properties props = System.getProperties();
		props.put("mail.smtp.host", serverHost);
		Session session = Session.getDefaultInstance(props, null);

		// create a new msg
		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(from));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to,
				false));
		msg.setSubject(subject);
		msg.setHeader("X-Mailer", "PowerPeering");
		msg.setSentDate(new Date());
		
		Multipart multipart = new MimeMultipart();
		MimeBodyPart messageBodyPart;
		// create the text part
		if(bodyText != null) {
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(bodyText);
			multipart.addBodyPart(messageBodyPart);
		}

		// create the attachment part
		if(attachment != null) {
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(attachment);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(attachment.getName());
			multipart.addBodyPart(messageBodyPart);
		}

		// Put parts in message
		if(bodyText != null || attachment != null)
			msg.setContent(multipart);
		else
			msg.setText(""); // if both are empty

		Transport.send(msg);
	}
}

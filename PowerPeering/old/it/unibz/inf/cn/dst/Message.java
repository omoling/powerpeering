package old.it.unibz.inf.cn.dst;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;

import old.it.unibz.inf.cn.Jumbo;
import old.it.unibz.inf.cn.thirtParty.Base64Coder;

public class Message {
	
	private EmailAdress from;
	private EmailAdress to;
	private String subject;
	private String content;
	private File attachment;
	
	public Message(EmailAdress from, EmailAdress to, String subject, String content) {
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.content = content;
		this.attachment = null;
	}	

	public Message(EmailAdress from, EmailAdress to, String subject, String content, File attachment) {
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.content = content;
		this.attachment = attachment;
	}
	
	protected Message() {}
	
	public EmailAdress getFrom() {
		return from;
	}
	
	protected void setFrom(EmailAdress from) {
		this.from = from;
	}
	
	public EmailAdress getTo() {
		return to;
	}
	
	protected void setTo(EmailAdress to) {
		this.to = to;
	}
	
	public String getSubject() {
		return subject;
	}
	
	protected void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getContent() {
		return content;
	}
	
	protected void setContent(String content) {
		this.content = content;
	}
	
	public boolean hasAttachment() {
		return attachment != null;
	}
	
	public File getAttachment() {
		return attachment;
	}
	
	protected void setAttachment(File attachment) {
		this.attachment = attachment;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("FROM: " + getFrom() + "\n");
		buf.append("TO: " + getTo() + "\n");
		buf.append("SUBJECT: " + getSubject() + "\n");
		buf.append("CONTENT: " + getContent() + "\n");
		buf.append("ATTACHMENT: " + (hasAttachment() ? "YES" : "NO"));
		
		return buf.toString();
	}
	
	public static Message parseMail(String mText) throws ParseException, IOException {
		String[] lines = mText.split("\n");
		String from = "";
		String to = "";
		String content = "";
		String subject = "";
		byte[] attachmentData = null;
		String attachmentName = "";
		
		for(int i = 0; i < lines.length; i++) {
			if(lines[i].startsWith("From:")) {
				from = lines[i].substring(lines[i].indexOf(" ")+1, lines[i].length());
			} else if(lines[i].startsWith("Delivered-To:")) {
				to = lines[i].substring(lines[i].indexOf(" ")+1, lines[i].length());
			} else if(lines[i].startsWith("Subject:")) {
				subject = lines[i].substring(lines[i].indexOf(" ")+1, lines[i].length());
			} else if(lines[i].startsWith("Content:")) {
				while(!lines[++i].equals(".")) {
					content += lines[i] + "\n";
				}
			} else if(lines[i].startsWith("Attachment:")) {
				attachmentName = lines[i].substring(lines[i].indexOf(" ")+1, lines[i].length());
				attachmentData = Base64Coder.decode(lines[++i]);
			}
		}
		
		if(attachmentData != null) {
			
			File f = new File(Jumbo.getTmpDir() + attachmentName);
			
			f.createNewFile();
			f.deleteOnExit(); // TODO only if resources ar moved afterwards
			
			FileOutputStream fout = new FileOutputStream(f);
			
			fout.write(attachmentData);
			fout.close();
			
			return new Message(EmailAdress.parseEmailAdress(from), EmailAdress.parseEmailAdress(to), subject, content, f);
		}
		
		return new Message(EmailAdress.parseEmailAdress(from), EmailAdress.parseEmailAdress(to), subject, content);
	}
}

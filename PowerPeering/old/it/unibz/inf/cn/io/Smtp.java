package old.it.unibz.inf.cn.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import old.it.unibz.inf.cn.Jumbo;
import old.it.unibz.inf.cn.dst.EmailAdress;
import old.it.unibz.inf.cn.dst.Message;
import old.it.unibz.inf.cn.thirtParty.Base64Coder;


public class Smtp {
	
	public static final int DEFAULT_PORT = 25;
	private static final int BUFFER_SIZE = 1024;
	
	public static void sendMail(String host, Message mail) throws Exception {
		
		Socket socket = null;
		DataOutputStream out = null;
		DataInputStream in =null;
		
		try {
			socket = new Socket(host, DEFAULT_PORT);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch(UnknownHostException e) {
			Jumbo.errLog("Error while sending mail: " + e);
			throw new Exception(e);
		} catch(IOException e) {
			Jumbo.errLog("Error while sending mail: " + e);
			throw new Exception(e);
		} catch(NullPointerException e) {
			Jumbo.errLog("Error while sending mail: " + e);
			throw new Exception(e);
		}
		
		if(out != null && in != null) {
			// send mail request
			try {
				out.writeBytes("HELO " + mail.getFrom().getDomain() + "\r\n");
				out.writeBytes("MAIL FROM: <" + mail.getFrom() + ">\r\n");
				out.writeBytes("RCPT TO: <" + mail.getTo() + ">\r\n");
				out.writeBytes("DATA\r\n");
				out.writeBytes("Subject: " + mail.getSubject() + "\r\n");
				if(mail.hasAttachment()) {
					out.writeBytes("Attachment: " + mail.getAttachment().getName() + "\r\n");
					FileInputStream fin = new FileInputStream(mail.getAttachment());
					byte[] buffer = new byte[BUFFER_SIZE];
					int dLen;
					while((dLen = fin.read(buffer)) != -1) {
						out.writeBytes(new String(Base64Coder.encode(buffer, dLen)));
					}
					out.writeBytes("\r\n");
				}
				out.writeBytes("Content:\r\n" + mail.getContent() + "\r\n");
				out.writeBytes("\r\n.\r\n");
				out.writeBytes("QUIT\r\n");
			} catch(IOException e) {
				Jumbo.errLog("Error while sending mail: " + e);
				throw new Exception(e);
			}
			// retrieve response
			String responseLine;
			try {
				
				BufferedReader bf = new BufferedReader(new InputStreamReader(in));
				while((responseLine = bf.readLine()) != null) {
					// TODO check for bad answers
					System.out.println("Server response: " + responseLine);
				}
			} catch(IOException e ) {
				Jumbo.errLog("Error while sending mail: " + e);
				throw new Exception(e);
			}			
		}
		
		try {
			if(in != null)
				in.close();
			if(out != null)
				out.close();
			if(socket != null)
				socket.close();
		} catch (IOException e) {
			Jumbo.errLog("Erro while closing socket/streams " + e);
			throw new Exception(e);
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		
		EmailAdress from = EmailAdress.parseEmailAdress("test@helo.com");
		EmailAdress to = EmailAdress.parseEmailAdress("default@cn.com");
		
		Message m = new Message(from, to, "subject", "content\r\ntesttext", new File(Jumbo.getDataPath() + "peers.xml"));
		
		Smtp.sendMail("localhost", m);
	}
	
	private Smtp() {}
	
}

package it.unibz.inf.cn.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import it.unibz.inf.cn.Jumbo;
import it.unibz.inf.cn.dst.Mail;

public class Pop3 {
	
	public static final int DEFAULT_PORT = 110;
	
	public static List<Mail> popMails(String host, String user, String pwd) throws Exception {
		
		Socket socket = null;
		DataOutputStream out = null;
		DataInputStream in =null;
		List<Mail> mails = null;
		
		try {
			socket = new Socket(host, DEFAULT_PORT);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch(UnknownHostException e) {
			Jumbo.ERR_LOG.println("Error while sending mail: " + e);
			throw new Exception(e);
		} catch(IOException e) {
			Jumbo.ERR_LOG.println("Error while sending mail: " + e);
			throw new Exception(e);
		} catch(NullPointerException e) {
			Jumbo.ERR_LOG.println("Error while sending mail: " + e);
			throw new Exception(e);
		}
		
		if(out != null && in != null) {
			// authenticate user
			authenticate(user, pwd, out, in);
			
			// fetch mails
			List<String> ids = fetchIDs(out, in);
			mails = fetchMails(out, in, ids);
			deleteMails(out, in, ids);
			
			
			try {
				out.writeBytes("QUIT\r\n");
			} catch(IOException e) {
				Jumbo.ERR_LOG.println("Error while quiting POP service: " + e);
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
			Jumbo.ERR_LOG.println("Erro while closing socket/streams " + e);
			throw new Exception(e);
		}

		return mails;		
	}
	
	private static boolean authenticate(String user, String pwd, DataOutputStream out, DataInputStream in) throws Exception{
		// authenticate
		out.writeBytes("USER " + user + "\r\n");
		out.writeBytes("PASS " + pwd + "\r\n");

		// read the resulting two lines from the in-stream
		String responseLine;
		BufferedReader bf = new BufferedReader(new InputStreamReader(in));
		int lineCnt = 0;
		while((responseLine = bf.readLine()) != null && lineCnt < 2) {
			if(!responseLine.startsWith("+OK"))	{
				Jumbo.ERR_LOG.println("Error while POP authentication");
				return false;
			}
			lineCnt++;
		}		
		return true;
	}
	
	private static List<String> fetchIDs(DataOutputStream out, DataInputStream in) throws IOException {
		List<String> ids = new ArrayList<String>();
		
		out.writeBytes("LIST\r\n");
		
		String responseLine;
		BufferedReader bf = new BufferedReader(new InputStreamReader(in));
		
		// check status line
		
		if(!bf.readLine().startsWith("+OK"))
			Jumbo.ERR_LOG.println("List command not successfull");
		
		while((responseLine = bf.readLine()) != null && !responseLine.equals(".")) {
			ids.add(responseLine.substring(0, responseLine.indexOf(" ")));
		}	
		
		return ids;
	}
	
	private static List<Mail> fetchMails(DataOutputStream out, DataInputStream in, List<String> ids) throws IOException {
		List<Mail> mails = new ArrayList<Mail>();
		
		BufferedReader bf = new BufferedReader(new InputStreamReader(in));
		String responseLine;
		StringBuffer textBuffer;
		for(String id : ids) {
			out.writeBytes("RETR " + id + "\r\n");
			textBuffer = new StringBuffer();
			responseLine = bf.readLine();
			if(!responseLine.startsWith("+OK")) {
				Jumbo.ERR_LOG.println("Error fetching mail!");
			}
			while((responseLine = bf.readLine()) != null && !responseLine.startsWith(".")) {
				textBuffer.append(responseLine + "\n");
			}
			try {
				mails.add(Mail.parseMail(textBuffer.toString()));
			} catch (ParseException e) {
				Jumbo.ERR_LOG.println("Error while parsing mail!");
			}
		}
		
		return mails;
	}
	
	private static void deleteMails(DataOutputStream out, DataInputStream in, List<String> ids) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(in));
		String responseLine;
		for(String id : ids) {
			out.writeBytes("DELE " + id + "\r\n");
			responseLine = bf.readLine();
			if(!responseLine.startsWith("+OK")) {
				Jumbo.ERR_LOG.println("Error deleting mail!");
			}
		}
	}
	
	private Pop3() {}
	
	public static void main(String[] args) throws Exception {
		List<Mail> mails = Pop3.popMails("localhost", "default", "pwd");
		
		for(Mail m : mails) {
			System.out.println(m);
		}
	}

}

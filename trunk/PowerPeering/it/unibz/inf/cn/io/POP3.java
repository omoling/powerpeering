package it.unibz.inf.cn.io;

import java.util.Properties;

import javax.mail.*;

public class POP3 {
	
	private static final int DEFAULT_PORT = 110;
	private static final String FOLDER_NAME = "INBOX";
	private static final String PROTOCOL_NAME = "pop3";
	
	public static void pop(String host, String user, String pwd, MessageHandler handler) throws MessagingException {
		
		if(handler == null)
			return;
		
		Properties props = System.getProperties();
		Session session = Session.getInstance(props, null);

		Store store = session.getStore(PROTOCOL_NAME);
		store.connect(host, DEFAULT_PORT, user, pwd);
		
		Folder folder = store.getDefaultFolder();
		folder = folder.getFolder(FOLDER_NAME);
		folder.open(Folder.READ_WRITE);
		
		for(Message m : folder.getMessages()) {
			if(handler.handleMessage(m))
				m.setFlag(Flags.Flag.DELETED, true);
		}
		
		folder.close(true);
		store.close();
	}
	
	public static void truncateMBox(String host, String user, String pwd) throws MessagingException {
		Properties props = System.getProperties();
		Session session = Session.getInstance(props, null);

		Store store = session.getStore(PROTOCOL_NAME);
		store.connect(host, DEFAULT_PORT, user, pwd);
		
		Folder folder = store.getDefaultFolder();
		folder = folder.getFolder(FOLDER_NAME);
		folder.open(Folder.READ_WRITE);
		
		for(Message m : folder.getMessages()) {
			m.setFlag(Flags.Flag.DELETED, true);
		}
		
		folder.close(true);
		store.close();
	}

}

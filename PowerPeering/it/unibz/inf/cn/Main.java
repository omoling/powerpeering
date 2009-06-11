package it.unibz.inf.cn;

import javax.mail.MessagingException;

public class Main {
	
	public static void main(String[] args) {
		
		if(args.length != 5) {
			usage();
			return;
		}
		
		String serverHost = args[0];
		String email = args[1];
		String user = args[2];
		String pwd = args[3];
		boolean log = Boolean.parseBoolean(args[4]);
		int ttl = 3;
		int checkTime = 3;
		int maxPeers = 10;
		
		PeerImpl peer = null;
		try {
			peer = new PeerImpl(serverHost, email, user, pwd, ttl, checkTime, maxPeers, log);
		} catch (MessagingException e) {
			System.out.println("Connection to server not possible");
			return;
		}
		
		peer.start();
		
	}
	
	public static void usage() {
		System.out.println("usage: java -jar PowerPeer.jar serverhost email username pwd log");
	}

}

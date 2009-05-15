package it.unibz.inf.cn;

public class Main {
	
	public static void main(String[] args) {
		
		if(args.length != 4) {
			usage();
			return;
		}
		
		String serverHost = args[0];
		String email = args[1];
		String user = args[2];
		String pwd = args[3];
		int ttl = 3;
		int checkTime = 3;
		int maxPeers = 10;
		
		PeerImpl peer = new PeerImpl(serverHost, email, user, pwd, ttl, checkTime, maxPeers);
		
		peer.start();
		
	}
	
	public static void usage() {
		System.out.println("usage: java -jar PowerPeer.jar serverhost email username pwd");
	}

}

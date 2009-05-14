package it.unibz.inf.cn;

public class Main {
	
	public static void main(String[] args) {
		
		String serverHost = "localhost";
		String email = "default@cn.com";
		String user = "default";
		String pwd = "pwd";
		int ttl = 3;
		int checkTime = 3;
		int maxPeers = 10;
		
		PeerImpl peer = new PeerImpl(serverHost, email, user, pwd, ttl, checkTime, maxPeers);
		
		peer.start();
		
	}

}

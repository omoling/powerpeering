package it.unibz.inf.cn.commands;

import java.util.ArrayList;
import java.util.List;

import it.unibz.inf.cn.PeerImpl;

public class PINGCommand implements Command {

	@Override
	public void execute(PeerImpl peerImpl) {
		
		// clear peers
		List<String> old_peers = new ArrayList<String>();
		old_peers.addAll(peerImpl.getPeers());
		peerImpl.getPeers().clear();
		
		for(String peer: old_peers) {
			try {
				peerImpl.sendPING(peer);
			} catch (Throwable t) {
				peerImpl.log(t.getMessage());
				peerImpl.error("Can't perform PING!\nSee log for details!");
			}
		}
	}

	@Override
	public String info() {
		return "Ping the network";
	}

}

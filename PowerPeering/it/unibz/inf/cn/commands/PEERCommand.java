package it.unibz.inf.cn.commands;

import it.unibz.inf.cn.PeerImpl;

public class PEERCommand implements Command {

	@Override
	public void execute(PeerImpl peerImpl) {
		peerImpl.display("Adjacend Peers: ");
		
		for(String peer : peerImpl.getPeers()) {
			peerImpl.display(peer);
		}
	}

	@Override
	public String info() {
		return "Display stored Peers";
	}

}

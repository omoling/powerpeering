package it.unibz.inf.cn.commands;

import it.unibz.inf.cn.PeerImpl;

public class PINGCommand implements Command {

	@Override
	public void execute(PeerImpl peerImpl) {
		
		for(String peer: peerImpl.getPeers()) {
			try {
				peerImpl.sendPING(peer);
			} catch (Throwable t) {
				peerImpl.log(t.getMessage());
				peerImpl.error("Can't perform PING!\nSee log for details!");
			}
		}
	}

}

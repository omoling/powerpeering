package it.unibz.inf.cn.commands;

import it.unibz.inf.cn.PeerImpl;

public class QUERYCommand implements Command {

	@Override
	public void execute(PeerImpl peerImpl) {
		String expression = peerImpl.mkInputRequest("Input query: ");
		
		for(String peer : peerImpl.getPeers()) {
			try {
				peerImpl.sendQUERY(peer, expression);
			} catch (Throwable t) {
				peerImpl.log(t.getMessage());
				peerImpl.error("Can't perform QUERY!\nSee log for details!");
			}
		}
	}

	@Override
	public String info() {
		return "Make a query by expression";
	}
}

package it.unibz.inf.cn.commands;

import it.unibz.inf.cn.PeerImpl;
import it.unibz.inf.cn.util.QueryHit;

public class QueryHitCommand implements Command {

	@Override
	public void execute(PeerImpl peerImpl) {
		
		if(peerImpl.getRecentQueryExpression() == null) {
			peerImpl.display("No recent query!");
			return;
		}
		
		peerImpl.display("QueryHits for expression: \"" + peerImpl.getRecentQueryExpression() + "\"");
		int i = 0;
		for(i = 0; i < peerImpl.getQueryHits().size(); i++) {
			QueryHit hit = peerImpl.getQueryHits().get(i);
			peerImpl.display(i+1 + " peer: " + hit.getPeer() + " resource: " + hit.getResource());
		}
		
		if(i == 0) {
			peerImpl.display("none!");
			return;
		}
		
		String input = peerImpl.mkInputRequest("Choose a resource to GET (or c for cancel): ");

		if(input.equals("c"))
			return;
		
		QueryHit hit = null;
		try {
			i = Integer.parseInt(input);
			hit = peerImpl.getQueryHits().get(i-1);
		} catch(Throwable t) {
			peerImpl.error("Not a valid input!");
			return;
		}
			
		try {
			peerImpl.sendGET(hit.getPeer(), hit.getResource());
		} catch (Throwable t) {
			peerImpl.error("Can't perform GET!\nSee log for details");
			peerImpl.log(t.getMessage());
		} 

		
	}

	@Override
	public String info() {
		return "Show the queryhits of the last query";
	}

}

package it.unibz.inf.cn.commands;

import it.unibz.inf.cn.PeerImpl;

public class RESOURCECommand implements Command {

	@Override
	public void execute(PeerImpl peerImpl) {
		peerImpl.display("Your resources: " );
		int i = 0;
		for(i = 0; i < peerImpl.getResources().size(); i++) {
			peerImpl.display(peerImpl.getResources().get(i));
		}
		
		if(i == 0)
			peerImpl.display("none!");
	}

	@Override
	public String info() {
		return "Display the shared resources";
	}

}

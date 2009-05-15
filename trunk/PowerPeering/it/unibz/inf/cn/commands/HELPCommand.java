package it.unibz.inf.cn.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unibz.inf.cn.PeerImpl;

public class HELPCommand implements Command {

	@Override
	public void execute(PeerImpl peerImpl) {
		
		List<String> list = new ArrayList<String>();
		list.addAll(peerImpl.getCommands().keySet());
		Collections.sort(list);
		
		int maxstrlen = 0;
		for(String s : list) {
			maxstrlen = Math.max(s.length(), maxstrlen);
		}

		for(String s : list) {
			String toDisplay = s;	
			for(int i = 0; i < maxstrlen-s.length()+3; i++)
				toDisplay += " ";
			peerImpl.display(toDisplay + peerImpl.getCommands().get(s).info());
		}
		
	}

	@Override
	public String info() {
		return "Display help";
	}
	
	

}

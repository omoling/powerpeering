package it.unibz.inf.cn.commands;

import it.unibz.inf.cn.PeerImpl;

public interface Command {
	
	public void execute(PeerImpl peerImpl);
	
	public String info();

}

package it.unibz.inf.cn.commands;

import it.unibz.inf.cn.PeerImpl;

public class QUITCommand implements Command {

	@Override
	public void execute(PeerImpl peerImpl) {
		peerImpl.display("bye!");
		peerImpl.stop();
	}

	@Override
	public String info() {
		return "Quit the app safely";
	}

}

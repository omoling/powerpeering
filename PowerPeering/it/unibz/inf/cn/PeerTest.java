package it.unibz.inf.cn;

import it.unibz.inf.cn.messages.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class PeerTest extends PowerPeer implements UserInterface {

	private List<String> peers;
	private int maxPeers;
	private List<String> resources;
	private static String DATA_PATH = "jumbo/";
	//private static String RESOURCE_PATH = "jumbo/resources";

	public PeerTest(String serverHost, String email, String user, String pwd,
			int ttl) {
		super(serverHost, email, user, pwd, ttl);
		peers = new ArrayList<String>();
		peers.add("default@cn.com");
		maxPeers = 10;
		loadResources();
	}

	public void handleMessage(PING message) {
		try {
			sendPONG(message.getFrom());
			if (message.getTTL() < 1)
				return;
			for (String peer : peers) {
				if (!peer.equalsIgnoreCase(message.getFrom()))
					sendFPING(peer, message.getFrom(), message.getTTL() - 1);
			}
		} catch (Throwable t) {
			log(t.getMessage());
			error("PING message could not be handled!");
		}
	}

	public void handleMessage(FPING message) {
		try {
			sendPONG(message.getRequestor());
			if (message.getTTL() < 1)
				return;
			for (String peer : peers) {
				if (!peer.equalsIgnoreCase(message.getFrom())
						&& !peer.equalsIgnoreCase(message.getRequestor()))
					sendFPING(peer, message.getFrom(), message.getTTL() - 1);
			}
		} catch (Throwable t) {
			log(t.getMessage());
			error("FPING message could not be handled!");
		}
	}

	public void handleMessage(PONG message) {
		if (peers.contains(message.getFrom()))
			return;
		if (peers.size() < maxPeers)
			peers.add(message.getFrom());
		else {
			// peer gets 1/maxPeers chance to be added
			Random r = new Random();
			if (r.nextInt(maxPeers+1) == 0) {
				peers.remove(r.nextInt(maxPeers));
				peers.add(message.getFrom());
			}
		}
	}

	public void handleMessage(QUERY message) {
		try {
			List<String> resources = searchResources(message.getExpression());
			if (resources.size() > 0)
				sendQUERYHIT(message.getFrom(), message.getExpression(), resources);
			if (message.getTTL() < 1)
				return;
			for (String peer : peers) {
				if (!peer.equalsIgnoreCase(message.getFrom()))
					sendFQUERY(peer, message.getFrom(),
							message.getExpression(), message.getTTL() - 1);
			}
		} catch (Throwable t) {
			log(t.getMessage());
			error("QUERY message could not be handled!");
		}
	}

	public void handleMessage(FQUERY message) {
		try {
			List<String> resources = searchResources(message.getExpression());
			if (resources.size() > 0)
				sendQUERYHIT(message.getRequestor(), message.getExpression(), resources);
			if (message.getTTL() < 1)
				return;
			for (String peer : peers) {
				if (!peer.equalsIgnoreCase(message.getFrom())
						&& !peer.equalsIgnoreCase(message.getRequestor()))
					sendFQUERY(peer, message.getFrom(),
							message.getExpression(), message.getTTL() - 1);
			}
		} catch (Throwable t) {
			log(t.getMessage());
			error("FQUERY message could not be handled!");
		}
	}

	public void handleMessage(QUERYHIT message) {
		// TODO handleMessage
		display("Queryhits for \"" + message.getExpression() + "\":");
		for(String resource : message.getResources())
			display(resource);
		
	}

	public void handleMessage(GET message) {
		display("Request: " + message.getResource() + " via GET");
	}

	public void handleMessage(POST message) {
		addResource(message.getAttachment().getName());
		display("Recieved Resource: " + message.getAttachment().getName());
	}

	@Override
	public void error(String s) {
		System.err.println("ERROR: " + s + "\n See log for details !");
	}

	@Override
	public void log(String s) {
		System.out.println("LOG: " + s);
	}

	@Override
	public void display(String s) {
		System.out.println(s);
	}
	
	private List<String> searchResources(String regex) {
		List<String> matches = new ArrayList<String>();
		for(String resource : resources) {
			if(resource.contains(regex))
				matches.add(resource);
		}
		return matches;
	}
	
	@SuppressWarnings("unchecked")
	private void loadResources() {
		resources = new ArrayList<String>();
		SAXReader reader = new SAXReader();
        Document document;
		try {
			document = reader.read(DATA_PATH + "resources.xml");
			List<Element> elements = document.getRootElement().elements("resource");
			for(Element e : elements) {
				resources.add(e.attributeValue("name"));
			}
		} catch (Throwable t) {
			log(t.getMessage());
			error("Can't read resource file!");
		}
	}
	
	private void addResource(String resourceName) {
		
		if(resources.contains(resourceName))
			return;
		
		SAXReader reader = new SAXReader();
        Document document;
		try {
			document = reader.read(DATA_PATH + "resources.xml");
			document.getRootElement().addElement("resource").addAttribute("name", resourceName);
			OutputStream fout = new FileOutputStream(DATA_PATH + "resources.xml");
			OutputFormat format = OutputFormat.createPrettyPrint();
	        XMLWriter writer = new XMLWriter( fout, format );
	        writer.write( document );
	        writer.close();
	        resources.add(resourceName);
		} catch (Throwable t) {
			log(t.getMessage());
			error("Can't read resource file!");
		}
	}

	public static void main(String[] args) throws AddressException,
			MessagingException, IOException, InterruptedException {

		PeerTest p = new PeerTest("localhost", "default@cn.com", "default",
				"pwd", 4);

		//p.sendPING("default@cn.com");
		//p.sendPONG("default@cn.com");
		//p.sendQUERYHIT("default@cn.com", "ha ha", new ArrayList<String>());
		//p.sendFPING("default@cn.com", "default@cn.com", 2);
		//p.sendFQUERY("default@cn.com", "default@cn.com", "ha", 2);
		//p.sendGET("default@cn.com", "resource");
		p.sendQUERY("default@cn.com", "d");
		//p.sendPOST("default@cn.com", new File("jumbo/resources.xml"));

		Thread.sleep(1000);

		p.startListener(2);

		Thread.sleep(10000);

		p.stopListener();

	}

}

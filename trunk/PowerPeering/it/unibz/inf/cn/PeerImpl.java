package it.unibz.inf.cn;

import it.unibz.inf.cn.commands.*;
import it.unibz.inf.cn.io.POP3;
import it.unibz.inf.cn.messages.*;
import it.unibz.inf.cn.util.QueryHit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class PeerImpl extends PowerPeer implements UserInterface {

	public static final String DEFAULT_PEER = "default@cn.com"; 
	
	private List<String> peers;
	private int maxPeers;
	private List<String> resources;
	private static String DATA_PATH = "jumbo/";
	private int checkTime;
	private static String RESOURCE_PATH = "jumbo/resources/";
	
	private Hashtable<String, Command> commands;
	// queryhits are handled through a list
	private List<QueryHit> queryHits;
	private String queryExpression;
	private boolean isStopped;

	public PeerImpl(String serverHost, String email, String user, String pwd,
			int ttl, int checkTime, int maxPeers) throws MessagingException {
		super(serverHost, email, user, pwd, ttl);
		peers = new ArrayList<String>();
		if(!email.equalsIgnoreCase(DEFAULT_PEER))
			peers.add(DEFAULT_PEER);
		this.maxPeers = maxPeers;
		this.checkTime = checkTime;
		loadResources();
		initCommands();
		
		POP3.truncateMBox(serverHost, user, pwd);
		
		isStopped = false;

		
	}
	
	public void mayAddUser(String peer) {
		if (peers.contains(peer))
			return;
		if (peers.size() < maxPeers)
			peers.add(peer);
		else {
			// peer gets 1/maxPeers chance to be added
			Random r = new Random();
			if (r.nextInt(maxPeers+1) == 0) {
				peers.remove(r.nextInt(maxPeers));
				peers.add(peer);
			}
		}
	}

	protected void handleMessage(PING message) {
		
		log("Received " + message);
		
		try {
			sendPONG(message.getFrom());
			if (message.getTTL() < 1)
				return;
			for (String peer : peers) {
				if (!peer.equalsIgnoreCase(message.getFrom()))
					sendFPING(peer, message.getFrom(), message.getTTL() - 1);
			}
			mayAddUser(message.getFrom());
		} catch (Throwable t) {
			log(t.getMessage());
			error("PING message could not be handled!");
		}
	}

	protected void handleMessage(FPING message) {
		
		log("Received " + message);
		
		try {			
			sendPONG(message.getRequestor());
			if (message.getTTL() < 1)
				return;
			for (String peer : peers) {
				if (!peer.equalsIgnoreCase(message.getFrom())
						&& !peer.equalsIgnoreCase(message.getRequestor()))
					sendFPING(peer, message.getRequestor(), message.getTTL() - 1);
			}
			mayAddUser(message.getFrom());
			mayAddUser(message.getRequestor());
		} catch (Throwable t) {
			log(t.getMessage());
			error("FPING message could not be handled!");
		}
	}

	protected void handleMessage(PONG message) {
		
		log("Received " + message);
		
		mayAddUser(message.getFrom());
	}

	protected void handleMessage(QUERY message) {
		
		log("Received " + message);
		
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
	
	public void sendQUERY(String to, String expression) throws AddressException, MessagingException {
		super.sendQUERY(to, expression);
		queryExpression = expression;
		queryHits = new ArrayList<QueryHit>();
	}
	
	public List<QueryHit> getQueryHits() {
		return queryHits;
	}
	
	public String getRecentQueryExpression() {
		return queryExpression;
	}

	protected void handleMessage(FQUERY message) {
		
		log("Received " + message);
		
		try {
			List<String> resources = searchResources(message.getExpression());
			if (resources.size() > 0)
				sendQUERYHIT(message.getRequestor(), message.getExpression(), resources);
			if (message.getTTL() < 1)
				return;
			for (String peer : peers) {
				if (!peer.equalsIgnoreCase(message.getFrom())
						&& !peer.equalsIgnoreCase(message.getRequestor()))
					sendFQUERY(peer, message.getRequestor(),
							message.getExpression(), message.getTTL() - 1);
			}
		} catch (Throwable t) {
			log(t.getMessage());
			error("FQUERY message could not be handled!");
		}
	}

	protected void handleMessage(QUERYHIT message) {
		
		log("Received " + message);
		
		for(QueryHit hit : queryHits) {
			if(hit.getPeer().equals(message.getFrom()))
				return;
		}
		
		if(message.getExpression().equals(queryExpression)) {
			for(String resource : message.getResources()) {
				queryHits.add(new QueryHit(message.getFrom(), resource));
			}
		}
	}

	protected void handleMessage(GET message) {
		
		log("Received " + message);
		
		File request = new File(RESOURCE_PATH + message.getResource());
		if(request.exists()) {
			try {
				sendPOST(message.getFrom(), request);
			} catch (Throwable t) {
				log(t.getMessage());
				error("Cant't handle GET request\nSee log for details!");
			}
		}
	}

	protected void handleMessage(POST message) {
		
		log("Received " + message);
		
		addResource(message.getAttachment().getName());
		display("Recieved Resource: " + message.getAttachment().getName());
	}

	@Override
	public void error(String s) {
		System.err.println("ERROR: " + s);
	}

	@Override
	public void log(String s) {
		System.out.println("LOG: " + s);
	}

	@Override
	public void display(String s) {
		System.out.println(s);
	}
	
	public String mkInputRequest(String msg) {
		System.out.print(msg);
		return new Scanner(System.in).nextLine();
	}
	
	public void start() {
		startListener(checkTime);
		display("Welcome to PowerPeering...");
		display("Type help for help");
		
		String input;
		Scanner scanner = new Scanner(System.in);
		
		do {
			System.out.print(getUser() + "$ ");
			input = scanner.nextLine().toLowerCase();
			
			if(commands.containsKey(input)) {
				commands.get(input).execute(this);
			} else {
				error("Wrong input command");
			}
			
		} while(!isStopped);
		
	}
	
	public void stop() {
		isStopped = true;
		stopListener();
	}
	
	public List<String> getPeers() {
		return peers;
	}
	
	public List<String> getResources() {
		return resources;
	}
	
	public Hashtable<String, Command> getCommands() {
		return commands;
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
	
	private void initCommands() {
		commands = new Hashtable<String, Command>();
		commands.put("ping", new PINGCommand());
		commands.put("query", new QUERYCommand());
		commands.put("hits", new QueryHitCommand());
		commands.put("quit", new QUITCommand());
		commands.put("peers", new PEERCommand());
		commands.put("resources", new RESOURCECommand());
		commands.put("help", new HELPCommand());
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
}

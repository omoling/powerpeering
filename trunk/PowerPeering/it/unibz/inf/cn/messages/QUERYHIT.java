package it.unibz.inf.cn.messages;

import java.util.ArrayList;
import java.util.List;

public class QUERYHIT extends PPMessage {
	
	private List<String> resources;
	private String expression;

	public QUERYHIT(String from, String to) {
		super(from, to);
		resources = new ArrayList<String>();
	}
	
	public void addResource(String resource) {
		this.resources.add(resource);
	}
	
	public List<String> getResources() {
		return resources;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public String getExpression() {
		return expression;
	}

	@Override
	public String getId() {
		return PPMessage.QUERYHIT_ID;
	}
	
	public String toString() {
		StringBuffer resourceList = new StringBuffer();
		for(int i = 0; i < resources.size(); i++) {
			if(i != 0)
				resourceList.append("|");
			resourceList.append(resources.get(i));
		}
		return super.toString() + " RESOURCES: " + resourceList;
	}

}

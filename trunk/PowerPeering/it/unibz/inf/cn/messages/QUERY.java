package it.unibz.inf.cn.messages;

public class QUERY extends PING {
	
	private String expression;

	public QUERY(String from, String to) {
		super(from, to);
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public String getExpression() {
		return expression;
	}
	
	@Override
	public String getId() {
		return PPMessage.QUERY_ID;
	}
	
	public String toString() {
		return super.toString() + " EXPRESSION: " + getExpression();
	}

}

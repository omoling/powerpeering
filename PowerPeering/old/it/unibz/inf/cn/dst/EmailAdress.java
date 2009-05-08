package old.it.unibz.inf.cn.dst;

import java.text.ParseException;

public class EmailAdress {
	
	private String user;
	private String domain;
	
	public EmailAdress(String user, String domain) {
		this.user = user;
		this.domain = domain;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public String toString() {
		return user + "@" + domain;
	}
	
	public static EmailAdress parseEmailAdress(String email) throws ParseException {
		
		// only some checks are implemented
		
		String[] tokens = email.split("@");
		
		if(tokens.length != 2)
			throw new ParseException("Not a valid email!", 0);
		
		String user = tokens[0];
		String domain = tokens[1];
		
		if(user.contains(" ") || domain.contains(" ") || !domain.contains("."))
			throw new ParseException("Not a valid email!", 0); 

		return new EmailAdress(user, domain);
	}

}

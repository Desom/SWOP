package domain.policies;

import java.util.ArrayList;

public class NonValidConfigurationException extends Exception{

	private static final long serialVersionUID = 1L;
	private ArrayList<String> messages;

	public NonValidConfigurationException(String message){
		this.messages = new ArrayList<String>();
		this.messages.add(message);
	}
	
	public void addMessage(String message){
		this.messages.add(message);
	}
}

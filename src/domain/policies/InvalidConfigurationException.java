package domain.policies;

import java.util.ArrayList;

public class InvalidConfigurationException extends Exception{

	private static final long serialVersionUID = 1L;
	private ArrayList<String> messages;

	public InvalidConfigurationException(String message){
		this.messages = new ArrayList<String>();
		this.messages.add(message);
	}
	
	public void addMessage(String message){
		this.messages.add(message);
	}
	
	@Override
	public String getMessage(){
		String message = "";
		for(String m : messages){
			message += m + "\n";
		}
		return message;
	}
}

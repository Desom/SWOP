package domain.policies;

import java.util.ArrayList;

public class InvalidConfigurationException extends Exception{

	private static final long serialVersionUID = 1L;
	private ArrayList<String> messages;

	/**
	 * Constructor of InvalidConfigurationException.
	 * 
	 * @param message
	 * 		The message of this new exception.
	 */
	public InvalidConfigurationException(String message){
		this.messages = new ArrayList<String>();
		this.messages.add(message);
	}

	/**
	 * Adds a message to this exception.
	 * 
	 * @param message
	 * 		The message to be added.
	 */
	public void addMessage(String message){
		this.messages.add(message);
	}
	
	/**
	 * Returns the message of this exception.
	 */
	@Override
	public String getMessage(){
		String message = "";
		for(String m : messages){
			message += m + "\n";
		}
		return message;
	}
}

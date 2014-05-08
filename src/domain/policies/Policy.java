package domain.policies;

import domain.configuration.Configuration;

/**
 * All policy classes inherit from this class.
 * This class enforces all policy classes to implement check and checkComplete.
 */
public abstract class Policy {
	
	private Policy successor;
	
	/**
	 * Constructor of Policy.
	 * 
	 * @param successor
	 * 		The next policy in the policy chain.
	 */
	public Policy(Policy successor) {
		this.successor = successor;
	}
	
	/**
	 * Proceeds to the next policy in the chain, if there is one.
	 * This method is only used for incomplete configurations.
	 * 
	 * @param configuration
	 * 		The incomplete configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the incomplete configuration is invalid.
	 */
	protected void proceed(Configuration configuration) throws InvalidConfigurationException{
		if (successor != null)
			successor.check(configuration);
	}
	
	/**
	 * Proceeds to the next policy in the chain, if there is one.
	 * This method is only used for completed configurations.
	 * 
	 * @param configuration
	 * 		The completed configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the completed configuration is invalid.
	 */
	protected void proceedComplete(Configuration configuration) throws InvalidConfigurationException{
		if (successor != null)
			successor.checkComplete(configuration);
	}
	
	/**
	 * Adds a message to the exception of this policy chain.
	 * 
	 * @param configuration
	 * 		The configuration which is being checked.
	 * @param message
	 * 		The message to be added to the exception.
	 * @throws InvalidConfigurationException
	 * 		If the configuration is invalid.
	 */
	protected void addToException(Configuration configuration, String message) throws InvalidConfigurationException {
		try{
			proceedComplete(configuration);
		}catch(InvalidConfigurationException e){
			e.addMessage(message);
			throw e;
		}
		throw new InvalidConfigurationException(message);
	}
	
	/**
	 * Checks whether the configuration is valid. If it isn't, it throws an exception.
	 * This method is only used for incomplete configurations.
	 * 
	 * @param configuration
	 * 		The incomplete configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the incomplete configuration is invalid.
	 */
	abstract public void check(Configuration configuration) throws InvalidConfigurationException;
	
	/**
	 * Checks whether the configuration is valid. If it isn't, it throws an exception.
	 * This method is only used for completed configurations.
	 * 
	 * @param configuration
	 * 		The completed configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the completed configuration is invalid.
	 */
	abstract public void checkComplete(Configuration configuration) throws InvalidConfigurationException;
}
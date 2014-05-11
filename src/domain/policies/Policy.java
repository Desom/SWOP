package domain.policies;

import domain.configuration.Configuration;

/**
 * All policy classes inherit from this class.
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
	// TODO probleem
	protected void addToException(Configuration configuration) throws InvalidConfigurationException {
		String message = this.buildMessage(configuration);
		try{
			proceed(configuration);
		}catch(InvalidConfigurationException e){
			e.addMessage(message);
			throw e;
		}
		throw new InvalidConfigurationException(message);
	}
	
	// TODO
	protected void addToExceptionComplete(Configuration configuration) throws InvalidConfigurationException {
		String message = this.buildMessage(configuration);
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
	public void checkComplete(Configuration configuration) throws InvalidConfigurationException {
		if (this.checkTest(configuration))
			this.proceedComplete(configuration);
		else
			this.addToExceptionComplete(configuration);
	}

	/**
	 * Checks whether a configuration is valid.
	 * 
	 * @param configuration
	 * 		The configuration to be checked.
	 * @return True if the configuration is valid, otherwise false.
	 */
	abstract protected boolean checkTest(Configuration configuration);
	
	/**
	 * Builds an exception message based on the given configuration.
	 * 
	 * @param configuration
	 * 		The configuration on which the message is based.
	 * @return A message indicating why the configuration is invalid.
	 */
	abstract protected String buildMessage(Configuration configuration);

}
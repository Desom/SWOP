package domain.policies;

import domain.configuration.Configuration;

public abstract class Policy {
	
	private Policy successor;
	
	public Policy(Policy successor) {
		this.successor = successor;
	}
	
	protected void proceed(Configuration configuration) throws InvalidConfigurationException{
		if (successor != null)
			successor.check(configuration);
	}
	
	protected void proceedComplete(Configuration configuration) throws InvalidConfigurationException{
		if (successor != null)
			successor.checkComplete(configuration);
	}
	
	abstract public void check(Configuration configuration) throws InvalidConfigurationException;
	
	abstract public void checkComplete(Configuration configuration) throws InvalidConfigurationException;
}
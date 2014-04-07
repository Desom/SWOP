package domain.policies;

import domain.configuration.Configuration;

public abstract class Policy {
	
	private Policy successor;
	
	public Policy(Policy successor) {
		this.successor = successor;
	}
	
	protected void proceed(Configuration configuration) throws NonValidConfigurationException{
		if (successor != null)
			successor.check(configuration);
	}
	
	protected void proceedComplete(Configuration configuration) throws NonValidConfigurationException{
		if (successor != null)
			successor.checkComplete(configuration);
	}
	
	abstract protected void check(Configuration configuration) throws NonValidConfigurationException;
	
	abstract protected void checkComplete(Configuration configuration) throws NonValidConfigurationException;
}
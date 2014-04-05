package domain.policies;

import domain.configuration.Configuration;

public abstract class Policy {
	
	private Policy successor;
	
	public Policy(Policy successor) {
		this.successor = successor;
	}
	
	protected boolean proceed(Configuration configuration) {
		if (successor != null)
			return successor.check(configuration);
		else
			return true;
	}
	
	protected boolean proceedComplete(Configuration configuration) {
		if (successor != null)
			return successor.checkComplete(configuration);
		else
			return true;
	}
	
	abstract protected boolean check(Configuration configuration);
	
	abstract protected boolean checkComplete(Configuration configuration);
}
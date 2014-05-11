package domain.policies;

import domain.configuration.Configuration;

/**
 * All policies which have to be checked at all times inherit from this class.
 */
public abstract class AlwaysPolicy extends Policy {

	public AlwaysPolicy(Policy successor) {
		super(successor);
	}

	/**
	 * Proceeds to the next policy in the chain if the checkTest-method returns true.
	 * Otherwise it will add the experienced problems to the exception message.
	 * 
	 * @param configuration
	 * 		The incomplete configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the incomplete configuration is invalid.
	 */
	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException {
		if (this.checkTest(configuration))
			this.proceed(configuration);
		else
			this.addToException(configuration);
	}

	@Override
	protected abstract boolean checkTest(Configuration configuration);
	
	@Override
	protected abstract String buildMessage(Configuration configuration);

}

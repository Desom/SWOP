package domain.policies;

import domain.configuration.Configuration;

/**
 * All policies which have to be checked when the configuration is completed inherit from this class.
 * 
 */
public abstract class CompletedPolicy extends Policy {

	/**
	 * Constructor of CompletedPolicy
	 * 
	 * @param successor
	 * 		The next policy in the policy chain.
	 */
	public CompletedPolicy(Policy successor) {
		super(successor);
	}

	/**
	 * Just proceeds to the next policy in the chain.
	 * 
	 * @param configuration
	 * 		The completed configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the completed configuration is invalid.
	 */
	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException {
		this.proceed(configuration);
	}

	@Override
	protected abstract boolean checkTest(Configuration configuration);

	@Override
	protected abstract String buildMessage(Configuration configuration);

}

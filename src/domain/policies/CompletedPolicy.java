package domain.policies;

import domain.configuration.Configuration;

/**
 * All policies which have to be checked when the configuration is completed inherit from this class.
 */
public abstract class CompletedPolicy extends Policy {

	public CompletedPolicy(Policy successor) {
		super(successor);
	}

	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException {
		this.proceed(configuration);
	}

	// TODO docs
	@Override
	protected abstract boolean checkTest(Configuration configuration);

	@Override
	protected abstract String buildMessage(Configuration configuration);

}

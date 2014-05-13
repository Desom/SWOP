package domain.policies;

import domain.configuration.Configuration;

/**
 * This policy checks if the configuration of a single task order has the right amount of options.
 *
 */
public class SingleTaskOrderNumbersOfTasksPolicy extends CompletedPolicy {

	/**
	 * Constructor of SingleTaskOrderNumbersOfTasksPolicy.
	 * 
	 * @param successor
	 * 		The next policy in the policy chain.
	 */
	public SingleTaskOrderNumbersOfTasksPolicy(Policy successor) {
		super(successor);
	}

	/**
	 * Checks whether the configuration has the right amount of options.
	 * 
	 * @param configuration
	 * 		The configuration to be checked.
	 * @return True if the configuration has the right amount of options, otherwise false.
	 */
	@Override
	protected boolean checkTest(Configuration configuration) {
		return configuration.getAllOptions().size() == 1;
	}
	
	/**
	 * Builds an exception message indicating how many options the single task order has.
	 * 
	 * @param configuration
	 * 		The configuration on which the message is based.
	 * @return An exception message indicating how many options the single task order has.
	 */
	@Override
	protected String buildMessage(Configuration configuration) {
		return "Deze configuratie heeft in plaats van 1 optie " +configuration.getAllOptions().size()+ " opties.";
	}
}

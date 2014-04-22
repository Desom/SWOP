package domain.policies;

import domain.configuration.Configuration;

/**
 * This policy checks if the configuration of a single task order has the right amount of options.
 *
 */
public class SingleTaskOrderNumbersOfTasksPolicy extends Policy{

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
	 * Just proceeds to the next policy in the chain, because this policy can't check incomplete configurations.
	 * 
	 * @param configuration
	 * 		The incomplete configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the incomplete configuration is invalid.
	 */
	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException {
		this.proceed(configuration);
	}

	/**
	 * Proceeds to the next policy in the chain if the configuration has the proper number of options (1).
	 * Otherwise it will throw an exception indicating there are too many options or modify an already thrown exception by another policy.
	 * 
	 * @param configuration
	 * 		The completed configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the completed configuration is invalid.
	 */
	@Override
	public void checkComplete(Configuration configuration) throws InvalidConfigurationException {
		if(configuration.getAllOptions().size() == 1){ 
			proceedComplete(configuration);
		}else{ 
			try{
				proceedComplete(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage("Deze configuratie heeft in plaats van 1 optie " +configuration.getAllOptions().size()+ " opties.");
				throw e;
			}
			throw new InvalidConfigurationException("Deze configuratie heeft in plaats van 1 optie " +configuration.getAllOptions().size()+ " opties.");
		}
	}
}

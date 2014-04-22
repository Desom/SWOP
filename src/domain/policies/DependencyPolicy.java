package domain.policies;

import java.util.ArrayList;

import domain.configuration.Configuration;
import domain.configuration.Option;

/**
 * This policy class checks if certain options in a configuration need other options and if the configuration has these options.
 *
 */
public class DependencyPolicy extends Policy {
	
	/**
	 * Constructor of DependencyPolicy
	 * 
	 * @param successor
	 * 		The next policy in the policy chain.
	 */
	public DependencyPolicy(Policy successor) {
		super(successor);
	}

	/**
	 * Returns a list of options which have still unfulfilled dependencies in the configuration.
	 * 
	 * @param configuration
	 * 		The configuration to be checked on unfulfilled dependencies.
	 * @return A list of all options which have still unfulfilled dependencies in the configuration.
	 */
	private ArrayList<Option> dependencyCheck(Configuration configuration){
		ArrayList<Option> optionsWithUnfulfilledDependencies = new ArrayList<Option>();
		for(Option option: configuration.getAllOptions()){
			if(!option.dependencyCheck(configuration.getAllOptions()))
				optionsWithUnfulfilledDependencies.add(option);
		}
		return optionsWithUnfulfilledDependencies;
	}

	/**
	 * Proceeds to the next policy in the chain if all dependencies are satisfied.
	 * Otherwise it will throw an exception indicating the remaining unfulfilled dependencies or modify an already thrown exception by another policy.
	 * 
	 * @param configuration
	 * 		The incomplete configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the incomplete configuration is invalid.
	 */
	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException {
		ArrayList<Option> conflictingOptions = dependencyCheck(configuration);
		if(conflictingOptions.isEmpty()){
			proceed(configuration);
		}else{
			try{
				proceed(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage(buildMessage(conflictingOptions));
				throw e;
			}
			throw new InvalidConfigurationException(buildMessage(conflictingOptions));
		}
	}

	/**
	 * Proceeds to the next policy in the chain if all dependencies are satisfied.
	 * Otherwise it will throw an exception indicating the remaining unfulfilled dependencies or modify an already thrown exception by another policy.
	 * 
	 * @param configuration
	 * 		The incomplete configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the incomplete configuration is invalid.
	 */
	@Override
	public void checkComplete(Configuration configuration) throws InvalidConfigurationException {
		ArrayList<Option> conflictingOptions = dependencyCheck(configuration);
		if(conflictingOptions.isEmpty()){
			proceedComplete(configuration);
		}else{
			try{
				proceedComplete(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage(buildMessage(conflictingOptions));
				throw e;
			}
			throw new InvalidConfigurationException(buildMessage(conflictingOptions));
		}
	}
	
	/**
	 * Builds an exception message indicating which options in the configuration are depending on missing options.
	 * 
	 * @param dependentOptions
	 * 		A list of options which depend on missing options
	 * @return An exception message indicating which options in the configuration are depending on missing options.
	 */
	private String buildMessage(ArrayList<Option> dependentOptions){
		String message = "Your configuration has options who's dependecies are not ok: \n";
		for(Option o : dependentOptions){
			message += "* " + o.toString() + "\n";
		}
		return message;
	}

}

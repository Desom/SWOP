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
			if(!option.dependancyCheck(configuration))
				optionsWithUnfulfilledDependencies.add(option);
		}
		return optionsWithUnfulfilledDependencies;
	}

	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException {
		ArrayList<Option> conflictingOptions = dependencyCheck(configuration);
		if(conflictingOptions.size() == 0){
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

	@Override
	public void checkComplete(Configuration configuration) throws InvalidConfigurationException {
		ArrayList<Option> conflictingOptions = dependencyCheck(configuration);
		if(conflictingOptions.size() == 0){
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
	
	
	private String buildMessage(ArrayList<Option> conflictingOptions){
		String message = "Your configuration has options who's dependecies are not ok: \n";
		for(Option o : conflictingOptions){
			message += "* " + o.toString() + "\n";
		}
		return message;
	}

}

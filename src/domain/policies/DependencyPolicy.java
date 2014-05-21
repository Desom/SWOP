package domain.policies;

import java.util.ArrayList;

import domain.configuration.Configuration;
import domain.configuration.taskables.Option;

/**
 * This policy class checks if certain options in a configuration need other options and if the configuration has these options.
 *
 */
public class DependencyPolicy extends CompletedPolicy {
	
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
	 * Checks whether the given configuration has no unfulfilled dependencies.
	 * 
	 * @param configuration
	 * 		The configuration to be checked.
	 * @return True if the configuration has no unfulfilled dependencies, otherwise false.
	 */
	@Override
	protected boolean checkTest(Configuration configuration) {
		return this.dependencyCheck(configuration).isEmpty();
	}
	
	/**
	 * Builds an exception message indicating which options in the configuration are depending on missing options.
	 * 
	 * @param configuration
	 * 		The configuration on which the message is based.
	 * @return An exception message indicating which options in the configuration are depending on missing options.
	 */
	@Override
	protected String buildMessage(Configuration configuration){
		String message = "Your configuration has options who's dependecies are not ok: \n";
		for(Option o : this.dependencyCheck(configuration)){
			message += "* " + o.toString() + "\n";
		}
		return message;
	}

}

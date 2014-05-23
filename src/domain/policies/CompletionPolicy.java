package domain.policies;

import java.util.ArrayList;

import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;

/**
 * This policy class checks if a configuration is complete (if it has all required option types).
 *
 */
public class CompletionPolicy extends CompletedPolicy {

	private ArrayList<OptionType> requiredTypes;

	/**
	 * Constructor of CompletionPolicy.
	 * 
	 * @param successor
	 * 		The next policy in the policy chain.
	 * @param requiredTypes
	 * 		The option types which are required for the configuration to be complete.
	 */
	public CompletionPolicy(Policy successor, ArrayList<OptionType> requiredTypes) {
		super(successor);
		this.requiredTypes = requiredTypes;
	}

	/**
	 * Returns a list of all option types which do not have an option yet.
	 * 
	 * @param configuration
	 * 		The configuration to be checked on unfulfilled option types.
	 * @return a list of all option types which do not have an option yet
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<OptionType> completionCheck(Configuration configuration){
		ArrayList<Option> allOptions = configuration.getAllOptions();
		ArrayList<OptionType> remainingTypes = (ArrayList<OptionType>) requiredTypes.clone();
		for (Option option : allOptions){
			remainingTypes.remove(option.getType());
		}
		return remainingTypes;
	}

	/**
	 * Checks whether the given configuration is complete.
	 * 
	 * @param configuration
	 * 		The configuration to be checked.
	 * @return True if the given configuration is complete, otherwise false.
	 */
	@Override
	protected boolean checkTest(Configuration configuration) {
		return this.completionCheck(configuration).isEmpty();
	}
	
	/**
	 * Builds an exception message indicating which option types in the configuration aren't fulfilled yet.
	 * 
	 * @param configuration
	 * 		The configuration on which the message is based.
	 * @return An exception message indicating which option types in the configuration aren't fulfilled yet.
	 */
	@Override
	protected String buildMessage(Configuration configuration){
		String message = "Your configuration is missing the following types: \n";
		for(OptionType t : this.completionCheck(configuration)){
			message += "* " + t.toString() + "\n";
		}
		return message;
	}
}
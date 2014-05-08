package domain.policies;

import java.util.ArrayList;

import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;

/**
 * This policy class checks if a configuration is complete (if it has all required option types).
 *
 */
public class CompletionPolicy extends Policy{

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
	 * Just proceeds to the next policy in the chain, because this policy can't check incomplete configurations for completeness.
	 * 
	 * @param configuration
	 * 		The incomplete configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the incomplete configuration is invalid.
	 */
	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException{
		proceed(configuration);
	}

	/**
	 * Proceeds to the next policy in the chain if there are no remaining option types to be fulfilled.
	 * Otherwise it will throw an exception indicating the remaining option types or modify an already thrown exception by another policy.
	 * 
	 * @param configuration
	 * 		The completed configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the completed configuration is invalid.
	 */
	@Override
	public void checkComplete(Configuration configuration) throws InvalidConfigurationException{
		ArrayList<OptionType> remainingTypes = completionCheck(configuration);
		if(remainingTypes.isEmpty())
			// als verdere policies iets gooien wordt er gewoon verder gegooid.
			proceedComplete(configuration);
		else
			// in dit geval moet gecatched worden en aangepast
			this.addToException(configuration, this.buildMessage(remainingTypes));
	}
	
	/**
	 * Builds an exception message indicating which option types in the configuration aren't fulfilled yet.
	 * 
	 * @param remainingTypes
	 * 		A list of remaining option types.
	 * @return An exception message indicating which option types in the configuration aren't fulfilled yet.
	 */
	private String buildMessage(ArrayList<OptionType> remainingTypes){
		String message = "Your configuration is missing the following types: \n";
		for(OptionType t : remainingTypes){
			message += "* " + t.toString() + "\n";
		}
		return message;
	}
}
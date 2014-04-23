package domain.policies;

import java.util.ArrayList;

import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;

/**
 * This policy checks if the configuration of a single task order has valid options.
 *
 */
public class SingleTaskOrderTaskTypePolicy extends Policy {

	private ArrayList<OptionType> validOptionTypes;
	
	/**
	 * Constructor of SingleTaskOrderTaskTypePolicy.
	 * 
	 * @param successor
	 * 		The next policy in the policy chain.
	 */
	public SingleTaskOrderTaskTypePolicy(Policy successor) {
		super(successor);
		this.validOptionTypes = OptionType.getAllSingleTaskPossibleTypes();
	}
	
	/**
	 * Returns a list of invalid options of this configuration.
	 * 
	 * @param configuration
	 * 		The configuration to be checked on invalid options.
	 * @return a list of invalid options of this configuration
	 */
	private ArrayList<Option> checkTypes(Configuration configuration) {
		ArrayList<Option> invalidOptions = new ArrayList<Option>();
		for(Option option : configuration.getAllOptions()){
			if(!this.validOptionTypes.contains(option.getType()))
				invalidOptions.add(option);
		}
		return invalidOptions;
	}

	/**
	 * Proceeds to the next policy in the chain if there are no invalid in the configuration.
	 * Otherwise it will throw an exception indicating the invalid options or modify an already thrown exception by another policy.
	 * 
	 * @param configuration
	 * 		The incomplete configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the incomplete configuration is invalid.
	 */
	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException {
		ArrayList<Option> invalidOptions = checkTypes(configuration);
		if(invalidOptions.isEmpty()){
			proceed(configuration);
		}else{
			try{
				proceed(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage(buildMessage(invalidOptions));
				throw e;
			}
			throw new InvalidConfigurationException(buildMessage(checkTypes(configuration)));
		}
	}

	/**
	 * Proceeds to the next policy in the chain if there are no invalid in the configuration.
	 * Otherwise it will throw an exception indicating the invalid options or modify an already thrown exception by another policy.
	 * 
	 * @param configuration
	 * 		The completed configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the completed configuration is invalid.
	 */
	@Override
	public void checkComplete(Configuration configuration) throws InvalidConfigurationException {
		ArrayList<Option> invalidOptions = checkTypes(configuration);
		if(invalidOptions.isEmpty()){
			proceedComplete(configuration);
		}else{
			try{
				proceedComplete(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage(buildMessage(invalidOptions));
				throw e;
			}
			throw new InvalidConfigurationException(buildMessage(checkTypes(configuration)));
		}
	}

	/**
	 * Builds an exception message indicating which options in the configuration are invalid.
	 * 
	 * @param invalidOptions
	 * 		A list of invalid options.
	 * @return An exception message indicating which options in the configuration are invalid.
	 */
	private String buildMessage(ArrayList<Option> invalidOptions) {
		String message = "Your configuration has options who's type are not ok: \n";
		for(Option o : invalidOptions){
			message += "* " + o.toString() + "\n";
		}
		return message;
	}
}

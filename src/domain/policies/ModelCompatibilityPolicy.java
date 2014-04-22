package domain.policies;

import java.util.ArrayList;

import domain.configuration.CarModel;
import domain.configuration.Configuration;
import domain.configuration.Option;

/**
 * This policy class checks if the options of a configuration are compatible with its model.
 *
 */
public class ModelCompatibilityPolicy extends Policy {

	/**
	 * Constructor of ModelCompatibilityPolicy.
	 * 
	 * @param successor
	 * 		The next policy in the policy chain.
	 */
	public ModelCompatibilityPolicy(Policy successor) {
		super(successor);
	}

	/**
	 * Returns a list of options of the given configuration which conflict with the car model.
	 *  
	 * @param configuration
	 * 		The configuration to be checked on conflicts.
	 * @return a list of options of the given configuration which conflict with the car model
	 */
	private ArrayList<Option> compatibilityCheck(Configuration configuration){
		ArrayList<Option> conflictingOptions = new ArrayList<Option>();
		
		CarModel model = configuration.getModel();
		ArrayList<Option> possibleOptions = model.getPossibleOptions();
		for(Option option : configuration.getAllOptions()){
			if(!possibleOptions.contains(option)){
				conflictingOptions.add(option);
			}
		}
		return conflictingOptions;
	}
	
	/**
	 * Proceeds to the next policy in the chain if there are no options conflicting with the car model in the configuration.
	 * Otherwise it will throw an exception indicating the conflicting options or modify an already thrown exception by another policy.
	 * 
	 * @param configuration
	 * 		The incomplete configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the incomplete configuration is invalid.
	 */
	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException{
		ArrayList<Option> conflictingOptions = compatibilityCheck(configuration);
		if(conflictingOptions.isEmpty()){
			proceed(configuration);
		}else{
			try{
				proceed(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage(buildMessage(configuration, conflictingOptions));
				throw e;
			}
			throw new InvalidConfigurationException(buildMessage(configuration, conflictingOptions));
		}
	}

	/**
	 * Proceeds to the next policy in the chain if there are no options conflicting with the car model in the configuration.
	 * Otherwise it will throw an exception indicating the conflicting options or modify an already thrown exception by another policy.
	 * 
	 * @param configuration
	 * 		The completed configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the completed configuration is invalid.
	 */
	@Override
	public void checkComplete(Configuration configuration) throws InvalidConfigurationException{
		ArrayList<Option> conflictingOptions = compatibilityCheck(configuration);
		if(conflictingOptions.isEmpty()){
			proceedComplete(configuration);
		}else{
			try{
				proceedComplete(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage(buildMessage(configuration, conflictingOptions));
				throw e;
			}
			throw new InvalidConfigurationException(buildMessage(configuration, conflictingOptions));
		}
	}
	
	/**
	 * Builds an exception message indicating which options in the configuration are conflicting with the car model.
	 * 
	 * @param conflictingOptions
	 * 		A list of options conflicting with the car model.
	 * @return An exception message indicating which options in the configuration are conflicting with the car model.
	 */
	private String buildMessage(Configuration configuration, ArrayList<Option> conflictingOptions){
		String message = "Your configuration has the following Options that are incompatible with the model: \n";
		message += "Your model is " + configuration.getModel().toString() + "\n";
		for(Option o : conflictingOptions){
			message += "* " + o.toString() + "\n";
		}
		return message;
	}

}

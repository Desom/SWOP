package domain.policies;

import java.util.ArrayList;

import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.VehicleModel;

/**
 * This policy class checks if the options of a configuration are compatible with its model.
 *
 */
public class ModelCompatibilityPolicy extends AlwaysPolicy {

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
	 * Returns a list of options of the given configuration which conflict with the vehicle model.
	 *  
	 * @param configuration
	 * 		The configuration to be checked on conflicts.
	 * @return a list of options of the given configuration which conflict with the vehicle model
	 */
	private ArrayList<Option> compatibilityCheck(Configuration configuration){
		ArrayList<Option> conflictingOptions = new ArrayList<Option>();
		
		VehicleModel model = configuration.getModel();
		ArrayList<Option> possibleOptions = model.getPossibleOptions();
		for(Option option : configuration.getAllOptions()){
			if(!possibleOptions.contains(option)){
				conflictingOptions.add(option);
			}
		}
		return conflictingOptions;
	}
	
	/**
	 * Checks whether the configuration has no options conflicting with the vehicle model.
	 * 
	 * @param configuration
	 * 		The configuration to be checked.
	 * @return True if the configuration has no options conflicting with the vehicle model, otherwise false.
	 */
	@Override
	protected boolean checkTest(Configuration configuration) {
		return this.compatibilityCheck(configuration).isEmpty();
	}
	
	/**
	 * Builds an exception message indicating which options in the configuration are conflicting with the vehicle model.
	 * 
	 * @param configuration
	 * 		The configuration on which the message is based.
	 * @return An exception message indicating which options in the configuration are conflicting with the vehicle model.
	 */
	@Override
	protected String buildMessage(Configuration configuration){
		String message = "Your configuration has the following Options that are incompatible with the model: \n";
		message += "Your model is " + configuration.getModel().toString() + "\n";
		for(Option o : this.compatibilityCheck(configuration)){
			message += "* " + o.toString() + "\n";
		}
		return message;
	}

}
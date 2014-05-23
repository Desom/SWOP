package domain.policies;

import java.util.ArrayList;

import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.configuration.VehicleCatalog;
import domain.configuration.Configuration;

/**
 * This policy checks if the configuration of a single task order has valid options.
 *
 */
public class SingleTaskOrderTaskTypePolicy extends AlwaysPolicy {

	private ArrayList<OptionType> validOptionTypes;
	
	/**
	 * Constructor of SingleTaskOrderTaskTypePolicy.
	 * 
	 * @param successor
	 * 		The next policy in the policy chain.
	 */
	public SingleTaskOrderTaskTypePolicy(Policy successor) {
		super(successor);
		this.validOptionTypes = VehicleCatalog.taskTypeCreator.getAllSingleTaskPossibleTypes();
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
	 * Checks whether the configuration has no invalid options.
	 * 
	 * @param configuration
	 * 		The configuration to be checked.
	 * @return True if the configuration has no invalid options, otherwise false.
	 */
	@Override
	protected boolean checkTest(Configuration configuration) {
		return this.checkTypes(configuration).isEmpty();
	}

	/**
	 * Builds an exception message indicating which options in the configuration are invalid.
	 * 
	 * @param configuration
	 * 		The configuration on which the message is based.
	 * @return An exception message indicating which options in the configuration are invalid.
	 */
	@Override
	protected String buildMessage(Configuration configuration) {
		String message = "Your configuration has options who's type are not ok: \n";
		for(Option o : this.checkTypes(configuration)){
			message += "* " + o.toString() + "\n";
		}
		return message;
	}
}

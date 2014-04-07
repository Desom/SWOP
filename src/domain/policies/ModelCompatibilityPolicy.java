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

	public ModelCompatibilityPolicy(Policy successor) {
		super(successor);
	}

	private ArrayList<Option> compatibilityCheck(Configuration configuration){
		ArrayList<Option> conflictingOptions = new ArrayList<Option>();
		
		CarModel model = configuration.getModel();
		ArrayList<Option> possibleOptions = model.getOptions();
		for(Option option : configuration.getAllOptions()){
			if(!possibleOptions.contains(option)){
				conflictingOptions.add(option);
			}
		}
		return conflictingOptions;
	}
	
	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException{
		ArrayList<Option> conflictingOptions = compatibilityCheck(configuration);
		if(conflictingOptions.size() == 0){
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

	@Override
	public void checkComplete(Configuration configuration) throws InvalidConfigurationException{
		ArrayList<Option> conflictingOptions = compatibilityCheck(configuration);
		if(conflictingOptions.size() == 0){
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
	
	private String buildMessage(Configuration configuration, ArrayList<Option> conflictingOptions){
		String message = "Your configuration has the following Options that are incompatible with the model: \n";
		message += "Your model is " + configuration.getModel().toString() + "\n";
		for(Option o : conflictingOptions){
			message += "* " + o.toString() + "\n";
		}
		return message;
	}

}

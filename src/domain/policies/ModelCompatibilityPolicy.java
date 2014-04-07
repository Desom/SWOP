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
	
	private CarModel model = null;
	private ArrayList<Option> conflictingOptions = new ArrayList<Option>();

	public ModelCompatibilityPolicy(Policy successor) {
		super(successor);
	}

	private boolean compatibilityCheck(Configuration configuration){
		this.model = configuration.getModel();
		ArrayList<Option> possibleOptions = model.getOptions();
		for(Option option : configuration.getAllOptions()){
			if(!possibleOptions.contains(option)){
				conflictingOptions.add(option);
			}
		}
		if(conflictingOptions.size() != 0){
			return false;
		}
		return true;
	}
	
	@Override
	protected void check(Configuration configuration) throws NonValidConfigurationException{
		if(compatibilityCheck(configuration)){
			proceed(configuration);
		}else{
			try{
				proceed(configuration);
			}catch(NonValidConfigurationException e){
				e.addMessage(buildMessage());
			}
			throw new NonValidConfigurationException(buildMessage());
		}
	}

	@Override
	protected void checkComplete(Configuration configuration) throws NonValidConfigurationException{
		if(compatibilityCheck(configuration)){
			proceedComplete(configuration);
		}else{
			try{
				proceedComplete(configuration);
			}catch(NonValidConfigurationException e){
				e.addMessage(buildMessage());
			}
			throw new NonValidConfigurationException(buildMessage());
		}
	}
	
	private String buildMessage(){
		String message = "Your configuration has the following Options that are incompatible with the model: \n";
		message += "Your model is " + this.model.toString() + "\n";
		for(Option o : conflictingOptions){
			message += "* " + o.toString() + "\n";
		}
		return message;
	}

}

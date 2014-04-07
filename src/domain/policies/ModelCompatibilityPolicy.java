package domain.policies;

import java.util.ArrayList;

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

	private boolean compatibilityCheck(Configuration configuration){
		ArrayList<Option> possibleOptions = configuration.getModel().getOptions();
		for(Option option : configuration.getAllOptions()){
			if(!possibleOptions.contains(option)){
				return false;
			}
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
				e.addMessage("The specified options are not compatible with the specified model");
			}
			throw new NonValidConfigurationException("The specified options are not compatible with the specified model");
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
				e.addMessage("The specified options are not compatible with the specified model");
			}
			throw new NonValidConfigurationException("The specified options are not compatible with the specified model");
		}
	}

}

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
	protected boolean check(Configuration configuration) {
		return compatibilityCheck(configuration) && proceed(configuration);
	}

	@Override
	protected boolean checkComplete(Configuration configuration) {
		return compatibilityCheck(configuration) && proceedComplete(configuration);
	}

}

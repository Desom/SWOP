package domain.policies;

import java.util.ArrayList;

import domain.configuration.Configuration;
import domain.configuration.Option;

/**
 * This policy class checks if certain options in a configuration need other options and if the configuration also has these options.
 *
 */
public class DependencyPolicy extends Policy {
	
	public DependencyPolicy(Policy successor) {
		super(successor);
	}


	private ArrayList<Option> dependencyCheck(Configuration configuration){
		ArrayList<Option> conflictingOptions = new ArrayList<Option>();
		for(Option i: configuration.getAllOptions()){
			if(!i.dependancyCheck(configuration))
				conflictingOptions.add(i);
		}
		
		return conflictingOptions;
	}

	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException {
		ArrayList<Option> conflictingOptions = dependencyCheck(configuration);
		if(conflictingOptions.size() == 0){
			proceed(configuration);
		}else{
			try{
				proceed(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage(buildMessage(conflictingOptions));
				throw e;
			}
			throw new InvalidConfigurationException(buildMessage(conflictingOptions));
		}
	}

	@Override
	public void checkComplete(Configuration configuration) throws InvalidConfigurationException {
		ArrayList<Option> conflictingOptions = dependencyCheck(configuration);
		if(conflictingOptions.size() == 0){
			proceedComplete(configuration);
		}else{
			try{
				proceedComplete(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage(buildMessage(conflictingOptions));
				throw e;
			}
			throw new InvalidConfigurationException(buildMessage(conflictingOptions));
		}
	}
	
	
	private String buildMessage(ArrayList<Option> conflictingOptions){
		String message = "Your configuration has options who's dependecies are not ok: \n";
		for(Option o : conflictingOptions){
			message += "* " + o.toString() + "\n";
		}
		return message;
	}

}

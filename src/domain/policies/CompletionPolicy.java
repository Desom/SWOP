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

	public CompletionPolicy(Policy successor, ArrayList<OptionType> requiredTypes) {
		super(successor);
		this.requiredTypes = requiredTypes;
	}

	private ArrayList<OptionType> completionCheck(Configuration configuration){
		ArrayList<Option> allOptions = configuration.getAllOptions();
		ArrayList<OptionType> remainingTypes = (ArrayList<OptionType>) requiredTypes.clone();
		for (Option option : allOptions){
			remainingTypes.remove(option.getType());
		}
		
		return remainingTypes;
	}

	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException{
		proceed(configuration);
	}

	@Override
	public void checkComplete(Configuration configuration) throws InvalidConfigurationException{
		ArrayList<OptionType> missingTypes = completionCheck(configuration);
		if(missingTypes.size() == 0){ // als verdere policies iets gooien wordt er gewoon verder gegooid.
			proceedComplete(configuration);
		}else{ // in dit geval moet gecatched worden en aangepast
			try{
				proceedComplete(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage(buildMessage(missingTypes));
				throw e;
			}
			throw new InvalidConfigurationException(buildMessage(missingTypes));
		}
	}
	
	
	
	private String buildMessage(ArrayList<OptionType> missingTypes){
		String message = "Your configuration is missing the following types: \n";
		for(OptionType t : missingTypes){
			message += "* " + t.toString() + "\n";
		}
		return message;
	}

}

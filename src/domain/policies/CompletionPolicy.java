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
	private ArrayList<OptionType> missingTypes;

	public CompletionPolicy(Policy successor, ArrayList<OptionType> requiredTypes) {
		super(successor);
		this.requiredTypes = requiredTypes;
	}

	private boolean completionCheck(Configuration configuration){
		ArrayList<Option> allOptions = configuration.getAllOptions();
		ArrayList<OptionType> remainingTypes = (ArrayList<OptionType>) requiredTypes.clone();
		for (Option option : allOptions){
			remainingTypes.remove(option.getType());
		}
		this.missingTypes = remainingTypes;
		return remainingTypes.isEmpty();
	}

	@Override
	protected void check(Configuration configuration) throws NonValidConfigurationException{
		proceed(configuration);
	}

	@Override
	protected void checkComplete(Configuration configuration) throws NonValidConfigurationException{
		if(completionCheck(configuration)){ // als verdere policies iets gooien wordt er gewoon verder gegooid.
			proceedComplete(configuration);
		}else{ // in dit geval moet gecatched worden en aangepast
			try{
				proceedComplete(configuration);
			}catch(NonValidConfigurationException e){
				e.addMessage(buildMessage());
				throw e;
			}
			throw new NonValidConfigurationException(buildMessage());
		}
	}
	
	
	
	private String buildMessage(){
		String message = "Your configuration is missing the following types: \n";
		for(OptionType t : missingTypes){
			message += "* " + t.toString() + "\n";
		}
		return message;
	}

}

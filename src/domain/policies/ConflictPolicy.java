package domain.policies;

import java.util.ArrayList;
import java.util.HashSet;

import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;

/**
 * This policy class checks if the configuration has no conflicting options.
 *
 */
public class ConflictPolicy extends Policy {
	
	public ConflictPolicy(Policy successor) {
		super(successor);
	}
	
	private HashSet<Option[]> conflictsCheck(Configuration configuration){
		HashSet<Option[]> conflictingOptions = new HashSet<Option[]>();
		ArrayList<Option> allOptions = configuration.getAllOptions();
		for (int i = 0; i < allOptions.size(); i++)
			for (int j = i + 1; j < allOptions.size(); j++)
				if (allOptions.get(i).conflictsWith(allOptions.get(j))){
					Option[] options = {allOptions.get(i), allOptions.get(j)};
					conflictingOptions.add(options);
				}
		
		return conflictingOptions;
	}
	
	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException{
		HashSet<Option[]> conflictingOptions = conflictsCheck(configuration);
		if(conflictingOptions.size() == 0){ // als verdere policies iets gooien wordt er gewoon verder gegooid.
			proceed(configuration);
		}else{ // in dit geval moet gecatched worden en aangepast
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
		HashSet<Option[]> conflictingOptions = conflictsCheck(configuration);
		if(conflictingOptions.size() == 0){ // als verdere policies iets gooien wordt er gewoon verder gegooid.
			proceedComplete(configuration);
		}else{ // in dit geval moet gecatched worden en aangepast
			try{
				proceedComplete(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage(buildMessage(conflictingOptions));
				throw e;
			}
			throw new InvalidConfigurationException(buildMessage(conflictingOptions));
		}
	}
	
	
	private String buildMessage(HashSet<Option[]> conflictingOptions){
		String message = "Your configuration has the following conflicting Options: \n";
		for(Option[] o : conflictingOptions){
			message += "* " + o[0].toString() + "conflicts with " + o[1].toString() + "\n";
		}
		return message;
	}
	
	
}

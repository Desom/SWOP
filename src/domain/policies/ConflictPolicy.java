package domain.policies;

import java.util.ArrayList;

import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;

/**
 * This policy class checks if the configuration has no conflicting options.
 *
 */
public class ConflictPolicy extends Policy {
	
	private ArrayList<Option[]> conflictingOptions = new ArrayList<Option[]>();
	
	public ConflictPolicy(Policy successor) {
		super(successor);
	}
	
	private boolean conflictsCheck(Configuration configuration){
		ArrayList<Option> allOptions = configuration.getAllOptions();
		for (int i = 0; i < allOptions.size(); i++)
			for (int j = i + 1; j < allOptions.size(); j++)
				if (allOptions.get(i).conflictsWith(allOptions.get(j))){
					Option[] options = {allOptions.get(i), allOptions.get(j)};
					conflictingOptions.add(options);
				}
		if(conflictingOptions.size() != 0){
			return false;
		}
		return true;
	}
	
	@Override
	protected void check(Configuration configuration) throws NonValidConfigurationException{
		if(conflictsCheck(configuration)){ // als verdere policies iets gooien wordt er gewoon verder gegooid.
			proceed(configuration);
		}else{ // in dit geval moet gecatched worden en aangepast
			try{
				proceed(configuration);
			}catch(NonValidConfigurationException e){
				e.addMessage(buildMessage());
				throw e;
			}
			throw new NonValidConfigurationException(buildMessage());
		}
	}

	@Override
	protected void checkComplete(Configuration configuration) throws NonValidConfigurationException {
		if(conflictsCheck(configuration)){ // als verdere policies iets gooien wordt er gewoon verder gegooid.
			proceedComplete(configuration);
		}else{ // in dit geval moet gecatched worden en aangepast
			try{
				proceedComplete(configuration);
			}catch(NonValidConfigurationException e){
				e.addMessage(buildMessage());
			}
			throw new NonValidConfigurationException(buildMessage());
		}
	}
	
	
	private String buildMessage(){
		String message = "Your configuration has the following conflicting Options: \n";
		for(Option[] o : conflictingOptions){
			message += "* " + o[0].toString() + "conflicts with " + o[1].toString() + "\n";
		}
		return message;
	}
	
	
}

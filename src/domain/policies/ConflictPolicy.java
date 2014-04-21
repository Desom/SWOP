package domain.policies;

import java.util.ArrayList;
import java.util.HashSet;

import domain.configuration.Configuration;
import domain.configuration.Option;

/**
 * This policy class checks if the configuration has no conflicting options.
 *
 */
public class ConflictPolicy extends Policy {
	
	/**
	 * Constructor of ConflictPolicy.
	 * 
	 * @param successor
	 * 		The next policy in the policy chain.
	 */
	public ConflictPolicy(Policy successor) {
		super(successor);
	}
	
	/**
	 * Returns a list of pairs of conflicting options in the given configuration.
	 * 
	 * @param configuration
	 * 		The configuration to be checked on conflicting options.
	 * @return a list of pairs of conflicting options in the given configuration.
	 */
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
	
	/**
	 * Proceeds to the next policy in the chain if there are no conflicting options in the configuration.
	 * Otherwise it will throw an exception indicating the conflicts or modify an already thrown exception by another policy.
	 * 
	 * @param configuration
	 * 		The incomplete configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the incomplete configuration is invalid.
	 */
	@Override
	public void check(Configuration configuration) throws InvalidConfigurationException{
		HashSet<Option[]> conflictingOptions = conflictsCheck(configuration);
		if(conflictingOptions.isEmpty()){ // als verdere policies iets gooien wordt er gewoon verder gegooid.
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

	/**
	 * Proceeds to the next policy in the chain if there are no conflicting options in the configuration.
	 * Otherwise it will throw an exception indicating the conflicts or modify an already thrown exception by another policy.
	 * 
	 * @param configuration
	 * 		The completed configuration to be checked.
	 * @throws InvalidConfigurationException
	 * 		If the completed configuration is invalid.
	 */
	@Override
	public void checkComplete(Configuration configuration) throws InvalidConfigurationException {
		HashSet<Option[]> conflictingOptions = conflictsCheck(configuration);
		if(conflictingOptions.isEmpty()){ // als verdere policies iets gooien wordt er gewoon verder gegooid.
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
	
	/**
	 * Builds an exception message indicating which options in the configuration are conflicting.
	 * 
	 * @param conflictingOptions
	 * 		A list of pairs of conflicting options.
	 * @return An exception message indicating which options in the configuration are conflicting.
	 */
	private String buildMessage(HashSet<Option[]> conflictingOptions){
		String message = "Your configuration has the following conflicting Options: \n";
		for(Option[] o : conflictingOptions){
			message += "* " + o[0].toString() + "conflicts with " + o[1].toString() + "\n";
		}
		return message;
	}
}

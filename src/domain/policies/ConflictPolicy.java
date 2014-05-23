package domain.policies;

import java.util.ArrayList;
import java.util.HashSet;

import domain.configuration.Configuration;
import domain.configuration.Option;

/**
 * This policy class checks if the configuration has no conflicting options.
 *
 */
public class ConflictPolicy extends AlwaysPolicy {
	
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
	 * Checks whether the given configuration has no conflicting options.
	 * 
	 * @param configuration
	 * 		The configuration to be checked.
	 * @return True if the given configuration has no conflicting options, otherwise false.
	 */
	@Override
	protected boolean checkTest(Configuration configuration) {
		return this.conflictsCheck(configuration).isEmpty();
	}
	
	/**
	 * Builds an exception message indicating which options in the configuration are conflicting.
	 * 
	 * @param configuration
	 * 		The configuration on which the message is based.
	 * @return An exception message indicating which options in the configuration are conflicting.
	 */
	@Override
	protected String buildMessage(Configuration configuration) {
		String message = "Your configuration has the following conflicting Options: \n";
		for(Option[] o : this.conflictsCheck(configuration)){
			message += "* " + o[0].toString() + " conflicts with " + o[1].toString() + "\n";
		}
		return message;
	}
}

package domain.policies;

import java.util.ArrayList;

import domain.configuration.Configuration;
import domain.configuration.Option;

/**
 * This policy class checks if the configuration has no conflicting options.
 *
 */
public class ConflictPolicy extends Policy {
	
	public ConflictPolicy(Policy successor) {
		super(successor);
	}
	
	private boolean conflictsCheck(Configuration configuration){
		ArrayList<Option> allOptions = configuration.getAllOptions();
		for (int i = 0; i < allOptions.size(); i++)
			for (int j = i + 1; j < allOptions.size(); j++)
				if (allOptions.get(i).conflictsWith(allOptions.get(j)))
					return false;
		return true;
	}
	
	@Override
	protected boolean check(Configuration configuration) {
		return conflictsCheck(configuration) && proceed(configuration);
	}

	@Override
	protected boolean checkComplete(Configuration configuration) {
		return conflictsCheck(configuration) && proceedComplete(configuration);
	}
}

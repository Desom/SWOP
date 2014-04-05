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

	@Override
	protected boolean check(Configuration configuration) {
		return proceed(configuration);
	}

	@Override
	protected boolean checkComplete(Configuration configuration) {
		ArrayList<Option> allOptions = configuration.getAllOptions();
		ArrayList<OptionType> remainingTypes = (ArrayList<OptionType>) requiredTypes.clone();
		for (Option option : allOptions)
			remainingTypes.remove(option.getType());
		if (remainingTypes.isEmpty())
			return proceed(configuration);
		else
			return false;
	}

}

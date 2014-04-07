package domain.policies;

import domain.configuration.Configuration;
import domain.configuration.Option;

/**
 * This policy class checks if certain options in a configuration need other options and if the configuration also has these options.
 *
 */
public class DependancyPolicy extends Policy {

	public DependancyPolicy(Policy successor) {
		super(successor);
	}
	
	@Override
	protected boolean check(Configuration configuration) {
		return proceed(configuration);
	}

	@Override
	protected boolean checkComplete(Configuration configuration) {
		for(Option i: configuration.getAllOptions()){
			if(!i.dependancyCheck(configuration)) return false;
		}
		return proceed(configuration);
	}

}

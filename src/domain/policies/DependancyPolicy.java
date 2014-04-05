package domain.policies;

import domain.configuration.Configuration;

/**
 * This policy class checks if certain options in a configuration need other options and if the configuration also has these options.
 *
 */
public class DependancyPolicy extends Policy {

	public DependancyPolicy(Policy successor) {
		super(successor);
	}
	
	// TODO implentatie in Option nodig om dependencies op te vragen, dependencies worden ook nog niet ingelezen.
	@Override
	protected boolean check(Configuration configuration) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkComplete(Configuration configuration) {
		// TODO Auto-generated method stub
		return false;
	}

}

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
	protected void check(Configuration configuration) {
		if(....(configuration)){
			proceed(configuration);
		}else{
			try{
				proceed(configuration);
			}catch(NonValidConfigurationException e){
				e.addMessage("There are options in the configuration that are dependent on other options that are not chosen.");
			}
			throw new NonValidConfigurationException("There are options in the configuration that are dependent on other options that are not chosen.");
		}
	}

	@Override
	protected void checkComplete(Configuration configuration) {
		if(....(configuration)){
			proceedComplete(configuration);
		}else{
			try{
				proceedComplete(configuration);
			}catch(NonValidConfigurationException e){
				e.addMessage("There are options in the configuration that are dependent on other options that are not chosen.");
			}
			throw new NonValidConfigurationException("There are options in the configuration that are dependent on other options that are not chosen.");
		}
	}

}

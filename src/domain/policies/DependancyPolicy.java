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


	private boolean checkDependencies(Configuration configuration){
		for(Option i: configuration.getAllOptions()){
			if(!i.dependancyCheck(configuration))
				return false;
		}
		return true;
	}

	@Override
	protected void check(Configuration configuration) throws NonValidConfigurationException {
		if(checkDependencies(configuration)){
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
	protected void checkComplete(Configuration configuration) throws NonValidConfigurationException {
		if(checkDependencies(configuration)){
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

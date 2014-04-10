package domain.policies;

import domain.configuration.Configuration;
/**
 *This policy checks if a singleTaskOrder has the right amount of options
 */
public class SingleTaskOrderNumbersOfTasksPolicy extends Policy{

	public SingleTaskOrderNumbersOfTasksPolicy(Policy successor) {
		super(successor);
	}

	@Override
	public void check(Configuration configuration)
			throws InvalidConfigurationException {
		this.proceed(configuration);
		
	}

	@Override
	public void checkComplete(Configuration configuration)
			throws InvalidConfigurationException {
		if(configuration.getAllOptions().size() != 1){ 
			proceedComplete(configuration);
		}else{ 
			try{
				proceedComplete(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage("Deze configuratie heeft in plaats van 1 optie " +configuration.getAllOptions().size()+ " opties.");
				throw e;
			}
			throw new InvalidConfigurationException("Deze configuratie heeft in plaats van 1 optie " +configuration.getAllOptions().size()+ " opties.");
		}
		
	}

}

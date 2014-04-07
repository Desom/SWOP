package domain.policies;

import java.util.ArrayList;

import domain.configuration.Configuration;
import domain.configuration.Option;

/**
 * This policy class checks if certain options in a configuration need other options and if the configuration also has these options.
 *
 */
public class DependancyPolicy extends Policy {

	private ArrayList<Option> conflictingOptions = new ArrayList<Option>();
	
	public DependancyPolicy(Policy successor) {
		super(successor);
	}


	private boolean checkDependencies(Configuration configuration){
		for(Option i: configuration.getAllOptions()){
			if(!i.dependancyCheck(configuration))
				conflictingOptions.add(i);
		}
		if(conflictingOptions.size() != 0){
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
				e.addMessage(buildMessage());
			}
			throw new NonValidConfigurationException(buildMessage());
		}
	}
	
	
	private String buildMessage(){
		String message = "Your configuration has options who's dependecies are not ok: \n";
		for(Option o : conflictingOptions){
			message += "* " + o.toString() + "\n";
		}
		return message;
	}

}

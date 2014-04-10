package domain.policies;

import java.util.ArrayList;

import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;

public class SingleTaskOrderTaskTypePolicy extends Policy {

	public SingleTaskOrderTaskTypePolicy(Policy successor) {
		super(successor);
	}

	@Override
	public void check(Configuration configuration)
			throws InvalidConfigurationException {
		if(CheckTypes(configuration).isEmpty()){
			proceed(configuration);
		}else{
			try{
				proceed(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage(buildMessage(CheckTypes(configuration)));
				throw e;
			}
			throw new InvalidConfigurationException(buildMessage(CheckTypes(configuration)));
		}
	}

	private String buildMessage(ArrayList<Option> checkTypes) {
		String message = "Your configuration has options who's type are not ok: \n";
		for(Option o : checkTypes){
			message += "* " + o.toString() + "\n";
		}
		return message;
	}

	private ArrayList<Option> CheckTypes(Configuration configuration) {
		ArrayList<Option> result = new ArrayList<Option>();
		for(Option i:configuration.getAllOptions()){
			if(i.getType() != OptionType.Seats && i.getType() != OptionType.Color) result.add(i);
		}
		return result;
	}

	@Override
	public void checkComplete(Configuration configuration)
			throws InvalidConfigurationException {
		if(CheckTypes(configuration).isEmpty()){
			proceedComplete(configuration);
		}else{
			try{
				proceedComplete(configuration);
			}catch(InvalidConfigurationException e){
				e.addMessage(buildMessage(CheckTypes(configuration)));
				throw e;
			}
			throw new InvalidConfigurationException(buildMessage(CheckTypes(configuration)));
		}

	}

}

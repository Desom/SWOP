package controller;


import java.util.ArrayList;
import java.util.List;

import domain.configuration.CarModel;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.policies.InvalidConfigurationException;
import domain.policies.Policy;

public class OurOrderform implements OrderForm{


	private Configuration configuration;
	
	public OurOrderform(CarModel model, Policy policies) throws IllegalArgumentException{
		this.configuration = new Configuration( model, policies );
	}
	
	/**
	 * Adds an option to the configuration of this form.
	 * 
	 * @throws InvalidConfigurationException
	 * 		If the configuration is invalid
	 */
	@Override
	public void addOption(Option option) throws InvalidConfigurationException {
		this.configuration.addOption(option);
	}
	
	/**
	 * Completes the configuration of this form.
	 * 
	 * @throws InvalidConfigurationException
	 * 		If the configuration is invalid
	 */
	@Override
	public void completeConfiguration() throws InvalidConfigurationException {
		this.configuration.complete();
	}

	@Override
	public List<Option> getPossibleOptionsOfType(OptionType type) {
		List<Option> result = new ArrayList<Option>();
		for(Option i:this.configuration.getModel().getOfOptionType(type)){
			result.add(i);
		}
		
		return result;
	}


	@Override
	public Configuration getConfiguration() {
		return this.configuration;
	}

	
	

}

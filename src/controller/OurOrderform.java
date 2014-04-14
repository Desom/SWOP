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

	private CommunicationTool controller;	
	
	public OurOrderform(CarModel model, Policy policies, CommunicationTool controller) throws IllegalArgumentException{
		this.controller = controller;
		this.configuration = new Configuration( model, policies );
	}
	
	/**
	 * Adds an option to the configuration of this form.
	 * 
	 * @throws InvalidConfigurationException
	 * 		If the configuration is invalid
	 */
	@Override
	public void addOption(String optionDescription) throws InvalidConfigurationException {
		Option option = controller.getOption(optionDescription);
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
	public List<String> getPossibleOptionsOfType(String type) {
		List<String> result = new ArrayList<String>();
		for(Option i:this.configuration.getModel().getOfOptionType(OptionType.valueOf(type))){
			result.add(i.toString());
		}
		
		return result;
	}

	@Override
	public List<String> getOptionTypes() {
		return this.controller.getOptionTypes();
	}
	@Override
	public Configuration getConfiguration() {
		return this.configuration;
	}

	
	

}

package controller;


import java.util.ArrayList;
import java.util.List;

import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.policies.InvalidConfigurationException;

public class OurOrderform implements OrderForm{
	
	private Configuration configuration;

	private CommunicationTool controller;	
	
	public OurOrderform(  Configuration configuration, CommunicationTool controller) throws IllegalArgumentException{
		this.controller = controller;
		this.configuration = configuration;
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

	
	

}

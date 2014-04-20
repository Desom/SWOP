package controller;

import java.util.List;

import domain.configuration.CarModel;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.policies.InvalidConfigurationException;
import domain.policies.Policy;

public abstract class OrderForm {

	private Configuration configuration;
	
	/**
	 * Constructor of OrderForm.
	 * 
	 * @param model
	 * 		The car model of the order of this order form.
	 * @param policies
	 * 		The policy chain which has to be checked.
	 */
	public OrderForm(CarModel model, Policy policies) {
		this.configuration = new Configuration(model, policies);
	}
	
	/**
	 * Completes the configuration of this form.
	 * 
	 * @throws InvalidConfigurationException
	 * 		If the configuration is invalid
	 */
	public void completeConfiguration() throws InvalidConfigurationException {
		this.configuration.complete();
	}
	
	/**
	 * Gets the configuration of this order form.
	 * 
	 * @return the configuration of this order form.
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}
	
	/**
	 * Adds an option to the configuration of this form.
	 * 
	 * @throws InvalidConfigurationException
	 * 		If the configuration is invalid.
	 */
	public void addOption(Option option) throws InvalidConfigurationException {
		this.configuration.addOption(option);
	}
	
	/**
	 * Returns a list of possible options of the given type.
	 * 
	 * @param type
	 * 		The option type of which the user wants to have the associated options.
	 * @return all possible options associated with the given options type
	 */
	abstract public List<Option> getPossibleOptionsOfType(OptionType type);
}

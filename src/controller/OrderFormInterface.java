package controller;

import java.util.List;

import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.policies.InvalidConfigurationException;


public interface OrderFormInterface {
	
	/**
	 * Stores an option in the form.
	 * @param opties description of the option you want to store
	 * @return true if you placed the option false otherwise
	 * **/
	
	public void addOption(Option opties) throws InvalidConfigurationException;
	
	/**
	 * Completes the configuration.
	 */
	public void completeConfiguration() throws InvalidConfigurationException;
	
	/**
	 * Get all possible options of a specified type.
	 * @param type
	 * @return
	 */
	public List<Option> getPossibleOptionsOfType(OptionType type);

	public Configuration getConfiguration();
}

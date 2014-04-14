package controller;

import java.util.ArrayList;
import java.util.List;

import domain.configuration.Configuration;
import domain.policies.InvalidConfigurationException;


public interface OrderForm {
	
	/**
	 * Stores an option in the form.
	 * @param option description of the option you want to store
	 * @return true if you placed the option false otherwise
	 * **/
	
	public void addOption(String option) throws InvalidConfigurationException;
	
	/**
	 * Completes the configuration.
	 */
	public void completeConfiguration() throws InvalidConfigurationException;
	
	/**
	 * Get all possible options of a specified type.
	 * @param type
	 * @return
	 */
	public List<String> getPossibleOptionsOfType(String type);

	public List<String> getOptionTypes();

	public Configuration getConfiguration();
}

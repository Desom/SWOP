package controller;

import java.util.ArrayList;
import java.util.List;

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
	/**
	 * See if you can place an option of a specified type.
	 * @param Type
	 * @return true if you can place an option of a specified type
	 */
	
	public boolean canPlaceType(String Type);
	/**
	 * get a list of all option types.
	 * @return a list of all option types.
	 */
	
	public List<String> getOptionTypes();
}

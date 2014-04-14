package controller;

import java.util.List;

import domain.configuration.Option;



public interface CommunicationTool {
	/**
	 * Get an option given the description.
	 * @param optionDescription
	 * 			the description of the option.
	 * @return the option matching the description.
	 */
	public Option getOption(String optionDescription);
	
	public List<String> getOptionTypes();
}

package domain.configuration;

import java.util.ArrayList;
import java.util.HashMap;

import domain.policies.InvalidConfigurationException;
import domain.policies.Policy;

public class Configuration {
	
	private final CarModel model;
	private ArrayList<Option> options;
	private Policy polucychain;
	private boolean iscomplete;
	/**
	 * Constructor of Configuration.
	 * 
	 * @param	model
	 * 			The car model used for this configuration
	 * @param	options
	 * 			The options used for this configuration
	 * @throws	IllegalArgumentException
	 * 			If the given options are conflicting with each other
	 */
	public Configuration(CarModel model, Policy policychain) throws IllegalArgumentException {
		this.model = model;
		this.options = new ArrayList<Option>();
		this.polucychain = policychain;
		this.iscomplete = false;
	}
	public void complete() throws InvalidConfigurationException{
		if(! this.iscomplete){
			this.polucychain.checkComplete(this);
			this.iscomplete = true;
		}
	}
	public void setOption(Option opt) throws InvalidConfigurationException {
		this.options.add(this.options.size(), opt);
		try {
			this.polucychain.check(this);
		} catch (InvalidConfigurationException e) {
			this.options.remove(this.options.size()-1);
			throw e;
		}
	}
	/**
	 * Returns all options of this configuration.
	 * 
	 * @return	the list of all options of the configuration
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Option> getAllOptions(){
		return (ArrayList<Option>) options.clone();
	}
	
	/**
	 * Get the expected time that will be spent working on completing all options taking into account the model of this configuration 
	 * 
	 * @return The amount of options multiplied with the expected time it takes to complete an option for the current model.
	 * 			If no model is specified, the default time per option is 60 minutes.
	 */
	public int getExpectedWorkingTime(){
		if(this.model == null){
			return 60*this.options.size();
		}
		return this.model.getExpectedTaskTime()*this.options.size();
	}


	/**
	 * Returns the string that represents this configuration.
	 */
	public String toString(){
		String s = model.toString() + "\n Options: \n";
		for(Option o: options){
			s += o.toString() + "\n";
		}
		return s;
	}
	
	/**
	 * Gives the option corresponding to the option type.
	 * 
	 * @param	optionType
	 * 			The string that specifies the option type
	 * @return	the option that corresponds to the option type
	 */
	public Option getOptionOfType(OptionType optionType){
		for(Option i:options) {
			if(i.getType() == optionType) return i;
		}
		return null;
	}

	/**
	 * Returns the CarModel from this configuration 
	 * 
	 * @return The CarModel of this configuration
	 */
	public CarModel getModel() {
		return model;
	}
}

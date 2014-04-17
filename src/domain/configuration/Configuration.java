package domain.configuration;

import java.util.ArrayList;

import domain.policies.InvalidConfigurationException;
import domain.policies.Policy;

public class Configuration {
	
	private final CarModel model;
	private ArrayList<Option> options;
	private Policy policychain;
	private boolean iscomplete;
	
	/**
	 * Constructor of Configuration.
	 * 
	 * @param model
	 * 		The car model used for this configuration
	 * @param policyChain
	 * 		The chain of policy to check whether this configuration is conform to the company policies
	 * @throws IllegalArgumentException
	 * 		If the given options are conflicting with each other
	 */
	public Configuration(CarModel model, Policy policyChain) throws IllegalArgumentException {
		this.model = model;
		this.options = new ArrayList<Option>();
		this.policychain = policyChain;
		this.iscomplete = false;
	}
	
	/**
	 * This method is called by to indicate that this configuration is complete.
	 * It will check if this configuration is valid.
	 * 
	 * @throws InvalidConfigurationException
	 * 		If this configuration is a valid one. 
	 */
	public void complete() throws InvalidConfigurationException{
		if(! this.iscomplete){
			this.policychain.checkComplete(this);
			this.iscomplete = true;
		}
	}
	
	/**
	 * Adds an option to this configuration while checking the policies to make sure this configuration is still valid.
	 * 
	 * @param option
	 * 		The option to be added
	 * @throws InvalidConfigurationException
	 * 		If the new configuration won't be valid if the option would be added
	 */
	public void addOption(Option option) throws InvalidConfigurationException {
		this.options.add(this.options.size(), option);
		try {
			this.policychain.check(this);
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
	 * Get the expected time that will be spent working per workstation taking into account the model of this configuration 
	 * 
	 * @return the expected time that will be spent working per workstation
	 */
	public int getExpectedWorkingTime(){
		if(this.model == null){
			return 60;
		}
		return this.model.getExpectedTaskTime();
	}


	/**
	 * Returns the string that represents this configuration.
	 */
	@Override
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
	
	//TODO is dit goed zo? of moet er equals(Object other) staan?
	// hashCode() moeten we niet maken denk ik? (stond ergens iets van dat dat nodig was als je equals override)
	public boolean equals(Configuration other){
		if(other == null){
			return false;
		}
		if(!this.getModel().equals(other.getModel())){
			return false;
		}
		if(this.getAllOptions().size() != other.getAllOptions().size()){
			return false;
		}
		//ik neem hier aan dat alle options verschillend zijn.
		for(Option option : this.getAllOptions()){
			if(!other.getAllOptions().contains(option)){
				return false;
			}
		}
		return true;
	}
}

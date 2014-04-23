package domain.configuration;

import java.util.ArrayList;

import domain.policies.InvalidConfigurationException;
import domain.policies.Policy;

public class Configuration {
	
	private final CarModel model;
	private final ArrayList<Option> options;
	private final Policy policyChain;
	private boolean isComplete;
	
	/**
	 * Constructor of Configuration.
	 * 
	 * @param model
	 * 		The car model used for this configuration.
	 * @param policyChain
	 * 		The chain of policies to check whether this configuration is conform to the company policies.
	 */
	public Configuration(CarModel model, Policy policyChain) {
		this.model = model;
		this.options = new ArrayList<Option>();
		if(this.policyChain == null){
			//exception docs
			throw new IllegalArgumentException("A Configuration always needs a Policy so that it can check it's own correctness.");
		}
		this.policyChain = policyChain;
		this.isComplete = false;
	}
	
	/**
	 * This method is called by to indicate that this configuration is complete.
	 * It will check if this configuration is valid.
	 * 
	 * @throws InvalidConfigurationException
	 * 		If this configuration is an invalid one. 
	 */
	public void complete() throws InvalidConfigurationException {
		if(!this.isComplete){
			this.checkCompletePolicies();
			this.isComplete = true;
		}
	}
	
	/**
	 * Adds an option to this configuration while checking the policies to make sure this configuration is still valid.
	 * 
	 * @param option
	 * 		The option to be added.
	 * @throws InvalidConfigurationException
	 * 		If the new configuration won't be valid if the option would be added.
	 */
	public void addOption(Option option) throws InvalidConfigurationException {
		this.options.add(this.options.size(), option);
		try {
			this.checkPolicies();
		} catch (InvalidConfigurationException e) {
			this.options.remove(this.options.size()-1);
			throw e;
		}
	}
	
	/**
	 * Returns all options of this configuration.
	 * 
	 * @return the list of all options of this configuration.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Option> getAllOptions(){
		return (ArrayList<Option>) options.clone();
	}
	
	/**
	 * Get the expected time that will be spent working per workstation taking into account the model of this configuration.
	 * 
	 * @return the expected time that will be spent working per workstation.
	 */
	public int getExpectedWorkingTime(){
		if(this.model == null){
			return 60;
		}
		return this.model.getExpectedTaskTime();
	}
	
	/**
	 * Gives the option corresponding to the option type.
	 * 
	 * @param optionType
	 * 		The option type of which the corresponding options has to be returned.
	 * @return The option that corresponds to the option type, but if there is none it returns null.
	 */
	public Option getOptionOfType(OptionType optionType){
		for(Option option : this.options) {
			if(option.getType() == optionType)
				return option;
		}
		return null;
	}

	/**
	 * Returns the car model of this configuration. 
	 * 
	 * @return The car model of this configuration.
	 */
	public CarModel getModel() {
		return this.model;
	}
	
	/**
	 * Checks whether this configuration and the given configuration are equal.
	 */
	@Override
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		if ((this.getClass() != obj.getClass())) {
			return false;
		}
		final Configuration other = (Configuration) obj;
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
	 * Checks if this configuration has already been successfully completed.
	 */
	public boolean isCompleted() {
		return isComplete;
	}
	
	/**
	 * Returns the chain of policies used by this configuration.
	 * 
	 * @return the chain of policies used by this configuration.
	 */
	public Policy getPolicyChain(){
		return this.policyChain;
	}
	
	/**
	 * TODO
	 * @throws InvalidConfigurationException
	 */
	public void checkPolicies() throws InvalidConfigurationException {
		this.policyChain.check(this);
	}
	
	/**
	 * TODO
	 * @throws InvalidConfigurationException
	 */
	public void checkCompletePolicies() throws InvalidConfigurationException {
		this.policyChain.checkComplete(this);
	}
	
}
package controller;

import java.util.List;

import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.policies.InvalidConfigurationException;
import domain.policies.Policy;

public class OurOrderform implements OrderForm{
	
	private Configuration configuration;
	private CarModelCatalog carModelCatalog;
	private CommunicationTool controller;
	
	
	public OurOrderform(String carModelName, Policy policyChain, CarModelCatalog carModelCatalog, CommunicationTool controller) throws IllegalArgumentException{
		this.controller = controller;
		this.carModelCatalog = carModelCatalog;
		CarModel carModel = this.getCarModel(carModelName);
		configuration = new Configuration(carModel, policyChain);
	}
	
	/**
	 * Adds an option to the configuration of this form.
	 * 
	 * @throws InvalidConfigurationException
	 * 		If the configuration is invalid
	 */
	@Override
	public void addOption(String optionDescription) throws InvalidConfigurationException {
		Option option = this.getOption(optionDescription);
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
	public List<String> getPossibleOptionsOfType( String type) {
 		return this.controller.getPossibleOptionsOfType(this, type);
	}

	@Override
	public boolean canPlaceType(String Type) {
		return this.controller.canPlaceType(this, Type);
	}

	@Override
	public List<String> getOptionTypes() {
		return this.controller.getOptionTypes();
	}
	
	/**
	 * Gets the car model associated with the given name.
	 * 
	 * @param carModelName
	 * 		The name of the car model
	 * @return The car model associated with the given name
	 * @throws IllegalArgumentException
	 * 		If there does not exist a car model associated with the given name
	 */
	private CarModel getCarModel(String carModelName) throws IllegalArgumentException {
		for(CarModel carModel : this.carModelCatalog.getAllModels()){
			if(carModel.getName().equals(carModelName)) 
				return carModel;
		}
		throw new IllegalArgumentException("This is not a valid car model name.");
	}
	
	/**
	 * Gets the option associated with the given description.
	 * 
	 * @param optionDescription
	 * 		The description of the option
	 * @return the option associated with the given description
	 * @throws IllegalArgumentException
	 * 		If there does not exist an option associated with the given description
	 */
	private Option getOption(String optionDescription) throws IllegalArgumentException {
		for(Option option: this.carModelCatalog.getAllOptions()){
			if(option.getDescription().equals(optionDescription))
				return option;
		}
		throw new IllegalArgumentException("This is not a valid option description.");
	}
}

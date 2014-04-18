package controller;

import java.util.ArrayList;
import java.util.List;

import domain.configuration.CarModel;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.policies.Policy;

public class CarOrderForm extends OrderForm {
	
	/**
	 * Constructor of CarOrderForm.
	 * 
	 * @param model
	 * 		The car model of the order of this order form.
	 * @param policies
	 * 		The policy chain which has to be checked.
	 */
	public CarOrderForm(CarModel model, Policy policies) {
		super(model, policies);
	}
	
	/**
	 * Returns a list of possible options of the given type.
	 */
	@Override
	public List<Option> getPossibleOptionsOfType(OptionType type) {
		List<Option> result = new ArrayList<Option>();
		for(Option i:this.configuration.getModel().getOfOptionType(type)){
			result.add(i);
		}
		return result;
	}
}

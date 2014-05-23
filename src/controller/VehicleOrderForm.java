package controller;

import java.util.ArrayList;
import java.util.List;

import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.configuration.VehicleModel;
import domain.policies.Policy;

public class VehicleOrderForm extends OrderForm {
	
	/**
	 * Constructor of VehicleOrderForm.
	 * 
	 * @param model
	 * 		The vehicle model of the order of this order form.
	 * @param policies
	 * 		The policy chain which has to be checked.
	 */
	public VehicleOrderForm(VehicleModel model, Policy policies) {
		super(model, policies);
	}
	
	/**
	 * Returns a list of possible options of the given type.
	 */
	@Override
	public List<Option> getPossibleOptionsOfType(OptionType type) {
		List<Option> result = new ArrayList<Option>();
		for(Option i:this.getConfiguration().getModel().getOfOptionType(type)){
			result.add(i);
		}
		return result;
	}
}

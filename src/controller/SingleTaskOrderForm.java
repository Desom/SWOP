package controller;

import java.util.List;

import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.policies.Policy;

public class SingleTaskOrderForm extends OrderForm{
	
	/**
	 * Constructor of SingleTaskOrderForm.
	 * 
	 * @param policies
	 * 		The policy chain which has to be checked.
	 */
	public SingleTaskOrderForm(Policy policies) {
		super(null, policies);
	}

	/**
	 * Returns a list of possible options of the given type.
	 */
	@Override
	public List<Option> getPossibleOptionsOfType(OptionType type) {
		// TODO Options nodig per OptionType
		return null;
	}

}

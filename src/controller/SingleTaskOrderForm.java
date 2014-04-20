package controller;

import java.util.ArrayList;
import java.util.List;

import domain.configuration.CarModelCatalog;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.policies.Policy;

public class SingleTaskOrderForm extends OrderForm{
	
	private CarModelCatalog catalog;
	
	/**
	 * Constructor of SingleTaskOrderForm.
	 * 
	 * @param catalog
	 * 		The car model catalog to find the possible options.
	 * @param policies
	 * 		The policy chain which has to be checked.
	 */
	public SingleTaskOrderForm(CarModelCatalog catalog, Policy policies) {
		super(null, policies);
		this.catalog = catalog;
	}

	/**
	 * Returns a list of possible options of the given type.
	 */
	@Override
	public List<Option> getPossibleOptionsOfType(OptionType type) {
		ArrayList<Option> possibleOptions = new ArrayList<Option>();
		for (Option option : this.catalog.getAllOptions())
			if (option.getType() == type)
				possibleOptions.add(option);
		return possibleOptions;
	}

}

package controller;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.configuration.VehicleCatalog;
import domain.policies.Policy;

public class SingleTaskOrderForm extends OrderForm{
	
	private VehicleCatalog catalog;
	private GregorianCalendar deadline;
	private GregorianCalendar currentTime;
	
	/**
	 * Constructor of SingleTaskOrderForm.
	 * 
	 * @param catalog
	 * 		The vehicle model catalog to find the possible options.
	 * @param policies
	 * 		The policy chain which has to be checked.
	 * @param currentTime
	 * 		The current time in the system.
	 */
	public SingleTaskOrderForm(VehicleCatalog catalog, Policy policies, GregorianCalendar currentTime) {
		super(null, policies);
		this.catalog = catalog;
		this.currentTime = (GregorianCalendar) currentTime.clone();
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
	
	/**
	 * Set the deadline for the SingleTaskOrderForm.
	 * 
	 * @param deadLine
	 * 		The deadline for the SingleTaskOrderForm.
	 */
	public void setDeadline(GregorianCalendar deadline){
		this.deadline = (GregorianCalendar) deadline.clone();
	}
	
	/**
	 * Returns the deadline of the SingleTaskOrderForm.
	 * 
	 * @return The deadline of the SingleTaskOrderForm.
	 */
	public GregorianCalendar getDeadline(){
		return (GregorianCalendar) this.deadline.clone();
	}

	/**
	 * Returns the current time of the system.
	 * 
	 * @return The current time of the system.
	 */
	public GregorianCalendar getCurrentTime() {
		return currentTime;
	}

}

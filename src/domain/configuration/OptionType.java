package domain.configuration;

import java.util.ArrayList;

public enum OptionType {
	Body, 
	Color(true,true), 
	Engine, 
	Gearbox,
	Seats(true,true),
	Wheels,
	Airco(false,false),
	Spoiler(false,false);

	private boolean singleTaskPossible;
	private boolean mandatory;

	/**
	 * Constructor of OptionType.
	 * 
	 * @param singleTaskPossible
	 * 		True if this option is available for single task orders, otherwise false.
	 * @param mandatory
	 * 		True if this option is mandatory for car orders, otherwise false.
	 */
	private OptionType(boolean singleTaskPossible, boolean mandatory){
		this.singleTaskPossible = singleTaskPossible;
		this.mandatory =mandatory;
	}

	/**
	 * Constructor of OptionType.
	 * The option type won't be available for single task orders and won't be mandatory for car orders.
	 */
	private OptionType(){
		this.singleTaskPossible = false;
		this.mandatory = true;
	}

	/**
	 * Returns whether the option type is available for single task orders.
	 * 
	 * @return true if the option type is available for single task orders, otherwise false.
	 */
	public boolean isSingleTaskPossible() {
		return singleTaskPossible;
	}
	
	/**
	 * Returns whether the option type is mandatory for car orders.
	 * 
	 * @return true if the option type is mandatory for car orders, otherwise false.
	 */
	public boolean isMandatory() {
		return mandatory;
	}
	
	/**
	 * Returns all option types that are available for single task orders.
	 * 
	 * @return all option types that are available for single task orders.
	 */
	public static ArrayList<OptionType> getAllSingleTaskPossibleTypes() {
		ArrayList<OptionType> singleTaskPossibleTypes = new ArrayList<OptionType>();
		for (OptionType optionType : OptionType.values())
			if (optionType.isSingleTaskPossible())
				singleTaskPossibleTypes.add(optionType);
		return singleTaskPossibleTypes;
	}
	
	/**
	 * Returns all option types that are mandatory for car orders.
	 * 
	 * @return all option types that are mandatory for car orders.
	 */
	public static ArrayList<OptionType> getAllMandatoryTypes(){
		ArrayList<OptionType> mandatoryTypes = new ArrayList<OptionType>();
		for (OptionType optionType : OptionType.values())
			if (optionType.isMandatory())
				mandatoryTypes.add(optionType);
		return mandatoryTypes;
	}
}
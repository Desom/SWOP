package domain.configuration;

import java.util.ArrayList;
import java.util.LinkedList;

public class OptionTypeCreator {

	private LinkedList<OptionType> optionTypes = new LinkedList<OptionType>(); 

	public OptionTypeCreator(){
		optionTypes.add(new OptionType("Body"));
		optionTypes.add(new OptionType("Color", true, true));
		optionTypes.add(new OptionType("Engine"));
		optionTypes.add(new OptionType("Gearbox"));
		optionTypes.add(new OptionType("Seats", true, true));
		optionTypes.add(new OptionType("Wheels"));
		optionTypes.add(new OptionType("Airco", false, false));
		optionTypes.add(new OptionType("Spoiler", false, false));
	}
	
	/**
	 * Return all OptionTypes.
	 * 
	 * @return a LinkedList of containing all OptionTypes.
	 */
	public LinkedList<OptionType> getAllTypes(){
		return optionTypes;
	}
	
	/**
	 * Returns all option types that are available for single task orders.
	 * 
	 * @return all option types that are available for single task orders.
	 */
	public ArrayList<OptionType> getAllSingleTaskPossibleTypes() {
		ArrayList<OptionType> singleTaskPossibleTypes = new ArrayList<OptionType>();
		for (OptionType optionType : optionTypes)
			if (optionType.isSingleTaskPossible())
				singleTaskPossibleTypes.add(optionType);
		return singleTaskPossibleTypes;
	}
	
	/**
	 * Returns all option types that are mandatory for vehicle orders.
	 * 
	 * @return all option types that are mandatory for vehicle orders.
	 */
	public ArrayList<OptionType> getAllMandatoryTypes(){
		ArrayList<OptionType> mandatoryTypes = new ArrayList<OptionType>();
		for (OptionType optionType : optionTypes)
			if (optionType.isMandatory())
				mandatoryTypes.add(optionType);
		return mandatoryTypes;
	}
	
	/**
	 * Returns the optiontype that has the specified name
	 * 
	 * @param name the name of the desired optionType
	 * @return the requested optiontype, or null if it does not exist
	 */
	public OptionType getOptionType(String name){
		for (OptionType optionType : optionTypes){
			if(optionType.getName().equals(name)){
				return optionType;
			}
		}
		return null;
	}
}

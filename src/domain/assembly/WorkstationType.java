package domain.assembly;

import java.util.LinkedList;

import domain.configuration.OptionType;

public class WorkstationType {
	
	private String name;
	private LinkedList<OptionType> acceptedOptionTypes;

	/**
	 * Constructor of WorkstationType.
	 * 
	 * @param name
	 * 		The name of this workstationType.
	 * @param acceptedOptionTypes
	 * 		A list of optionTypes that can be processed on this type of workstation.
	 */
	WorkstationType(String name, LinkedList<OptionType> acceptedOptionTypes){
		this.name = name;
		this.acceptedOptionTypes = new LinkedList<OptionType>(acceptedOptionTypes);
	}
	
	/**
	 * Returns a LinkedList of all option types that can be processed on this type of workstation.
	 * 
	 * @return a LinkedList of all option types that can be processed on this type of workstation.
	 */
	public LinkedList<OptionType> getacceptedOptionTypes(){
		return new LinkedList<OptionType>(acceptedOptionTypes);
	}
	
	/**
	 * Return the name of this workstationType
	 * 
	 * @return the name of this workstationType
	 */
	public String getName(){
		return name;
	}
	
	@Override
	public String toString(){
		return name;
	}
}

package domain.configuration;

import java.util.ArrayList;

public class Option {
	
	private String description; 
	private ArrayList<Option> incompatibles;
	private OptionType type;
	private ArrayList<ArrayList<Option>> dependencies;// elke arrayList bevat een lijst van opties waarvan minstens 1 aanwezig moet zijn voor correctheid
	
	/**
	 * Constructor of Option.
	 * 
	 * @param description
	 * 		The description of the option.
	 * @param type
	 * 		The type of this option.
	 * @throws VehicleModelCatalogException
	 * 		If the description is null.
	 * 		If the type is null.
	 */
	public Option(String description, OptionType type) throws VehicleModelCatalogException {
		if(description == null || type == null)
			throw new VehicleModelCatalogException("null in non null value of Option");
		this.incompatibles = new ArrayList<Option>();
		this.description = description;
		this.type = type;
		incompatibles.add(this);
		this.dependencies = new ArrayList<ArrayList<Option>>();
	}

	/**
	 * Returns the description of this option.
	 * 
	 * @return the description of this option
	 */
	public String getDescription(){
		return description;
	}
	
	/**
	 * Adds an incompatible option to this option.
	 * 
	 * @param option
	 * 		The incompatible option.
	 */
	void addIncompatible(Option option){
		if(!this.incompatibles.contains(option))
			this.incompatibles.add(option);
	}
	
	/**
	 * Adds options of which this option is dependent of.
	 * 
	 * @param dependency
	 * 		The new dependency to be added.
	 */
	void addDependancy(ArrayList<Option> dependency){
		this.dependencies.add(dependency);
	}
	
	/**
	 * Checks whether this option conflicts with the given option.
	 * 
	 * @param other
	 * 		The other option.
	 * @return true if this option conflicts with the other option, otherwise false
	 */
	public Boolean conflictsWith(Option other){
		if(type == other.getType())
			return true;
		if(this.incompatibles.contains(other))
			return true;
		return false;
	}

	/**
	 * Returns the type of this option.
	 * 
	 * @return the type of this option
	 */
	public OptionType getType(){
		return type;
	}
	
	/**
	 * Checks whether a list of options satisfies all dependencies of this option.
	 * 
	 * @param allOptions
	 * 		The list that has to be checked to satisfy all dependencies of this option.
	 */
	public boolean dependencyCheck(ArrayList<Option> allOptions){
		loop : for(ArrayList<Option> dependency : dependencies){
			for(Option option : dependency)
				if(allOptions.contains(option))
					continue loop;
			return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return description;
	}
}


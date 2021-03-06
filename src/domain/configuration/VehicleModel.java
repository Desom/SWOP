package domain.configuration;
import java.util.ArrayList;
import java.util.HashMap;

import domain.assembly.workstations.WorkstationType;


public class VehicleModel {
	
	private final String name;
	private final ArrayList<Option> possibleOptions;
	private final ArrayList<Part> parts;
	private final HashMap<WorkstationType, Integer> workstationTimes;
	
	/**
	 * Constructor of VehicleModel.
	 * 
	 * @param name
	 * 		Name of the vehicle model.
	 * @param possibleOptions
	 * 		The possible options for this model.
	 * @param workstationTimes
	 * 		The times each workstation needs to work on this model.
	 * @throws VehicleCatalogException
	 * 		If the name or possibleOptions equals null or if an option type is not represented in the possibleOptions.
	 */
	public VehicleModel(String name, ArrayList<Option> possibleOptions, ArrayList<Part> parts, HashMap<WorkstationType, Integer> workstationTimes) throws VehicleCatalogException{
		if(name == null || possibleOptions == null)
			throw new VehicleCatalogException("null in non null value of Model");

		for(OptionType type:VehicleCatalog.taskTypeCreator.getAllOptionTypes()){
			if(type.isMandatory() && !existstype(type, possibleOptions))
				throw new VehicleCatalogException("Missing type: "+ type);
		}
		this.possibleOptions = possibleOptions;
		this.parts = parts;
		this.name = name;
		this.workstationTimes = workstationTimes;
	}
	
	
	/**
	 * Checks if a the given option type is represented in the given list of options.
	 * 
	 * @param type
	 * 		The type which has to represented.
	 * @param options
	 * 		The options which have to represent the option type.
	 * @return true if an option with the type is found, otherwise false
	 */
	private boolean existstype(OptionType type, ArrayList<Option> options) {
		for(Option option : options)
			if (option.getType().equals(type))
				return true;
		return false;
	}

	/**
	 * Returns a list of possible options for this vehicle model.
	 * 
	 * @return A list of possible options for this vehicle model.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Option> getPossibleOptions() {
		return (ArrayList<Option>) possibleOptions.clone();
	}

	/**
	 * Returns the name of this vehicle model.
	 * 
	 * @return The name of this vehicle model.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns all possible options of a specific type.
	 * 
	 * @param type
	 * 		The type of which all possible options have to be returned.
	 * @return All possible options of the given type.
	 */
	public ArrayList<Option> getOfOptionType(OptionType type) {
		ArrayList<Option> optionsOfType = new ArrayList<Option>();
		for(Option option : possibleOptions){
			if(option.getType().equals(type))
				optionsOfType.add(option);
		}
		return optionsOfType;
	}
	
	/**
	 * Returns the expected working time spent on a task for this model on the specified workstationType.
	 * 
	 * @return The expected time it takes to complete a task on the specified workstationType.
	 */
	public int getExpectedTaskTime(WorkstationType type){
		Integer time = workstationTimes.get(type);
		if(time == null)
			return 60;
		return time;
	}
	
	/**
	 * Returns the parts this model has.
	 * 
	 * @return The parts of this model.
	 */
	public ArrayList<Part> getParts(){
		return this.parts;
	}
	
	@Override
	public String toString(){
		return name;
	}
}

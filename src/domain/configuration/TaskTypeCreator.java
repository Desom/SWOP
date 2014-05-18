package domain.configuration;

import java.util.ArrayList;
import java.util.LinkedList;

public class TaskTypeCreator {

	private LinkedList<TaskType> optionTypes = new LinkedList<TaskType>(); 
	public OptionType Body;
	public OptionType Color;
	public OptionType Engine;
	public OptionType Gearbox;
	public OptionType Seats;
	public OptionType Wheels;
	public OptionType Airco;
	public OptionType Spoiler;
	
	public Part ToolStorage;
	public Part CargoProtection;
	

	public TaskTypeCreator(){
		this.Body = new OptionType("Body");
		optionTypes.add(this.Body);
		this.Color = new OptionType("Color", true, true);
		optionTypes.add(this.Color);
		this.Engine = new OptionType("Engine");
		optionTypes.add(this.Engine);
		this.Gearbox = new OptionType("Gearbox");
		optionTypes.add(this.Gearbox);
		this.Seats = new OptionType("Seats", true, true);
		optionTypes.add(this.Seats);
		this.Wheels = new OptionType("Wheels");
		optionTypes.add(this.Wheels);
		this.Airco = new OptionType("Airco", false, false);
		optionTypes.add(this.Airco);
		this.Spoiler = new OptionType("Spoiler", false, false);
		optionTypes.add(this.Spoiler);
		
		//TODO add the 2 parts as well!
	}
	
	/**
	 * Return all TaskTypes.
	 * 
	 * @return a LinkedList of containing all TaskTypes.
	 */
	public LinkedList<TaskType> getAllTypes(){
		return optionTypes;
	}
	
	/**
	 * Returns all task types that are available for single task orders.
	 * 
	 * @return all task types that are available for single task orders.
	 */
	public ArrayList<TaskType> getAllSingleTaskPossibleTypes() {
		ArrayList<TaskType> singleTaskPossibleTypes = new ArrayList<TaskType>();
		for (TaskType optionType : optionTypes)
			if (optionType.isSingleTaskPossible())
				singleTaskPossibleTypes.add(optionType);
		return singleTaskPossibleTypes;
	}
	
	/**
	 * Returns all task types that are mandatory for vehicle orders.
	 * 
	 * @return all task types that are mandatory for vehicle orders.
	 */
	public ArrayList<TaskType> getAllMandatoryTypes(){
		ArrayList<TaskType> mandatoryTypes = new ArrayList<TaskType>();
		for (TaskType optionType : optionTypes)
			if (optionType.isMandatory())
				mandatoryTypes.add(optionType);
		return mandatoryTypes;
	}
	
	/**
	 * Returns the tasktype that has the specified name
	 * 
	 * @param name the name of the desired taskType
	 * @return the requested tasktype, or null if it does not exist
	 */
	public TaskType getTaskType(String name){
		for (TaskType optionType : optionTypes){
			if(optionType.getName().equals(name)){
				return optionType;
			}
		}
		return null;
	}

	
}

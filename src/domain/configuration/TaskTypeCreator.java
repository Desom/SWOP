package domain.configuration;

import java.util.ArrayList;
import java.util.LinkedList;

public class TaskTypeCreator {

	private LinkedList<OptionType> optionTypes = new LinkedList<OptionType>(); 
	private LinkedList<PartType> partTypes = new LinkedList<PartType>(); 
	public OptionType Body;
	public OptionType Color;
	public OptionType Engine;
	public OptionType Gearbox;
	public OptionType Seats;
	public OptionType Wheels;
	public OptionType Airco;
	public OptionType Spoiler;
	
	public PartType ToolStorage;
	public PartType CargoProtection;
	public PartType Certification;
	

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
		this.ToolStorage = new PartType("ToolStorage");
		partTypes.add(this.ToolStorage);
		this.CargoProtection = new PartType("CargoProtection");
		partTypes.add(CargoProtection);
		this.Certification = new PartType("Certification");
		partTypes.add(Certification);
	}
	
	/**
	 * Return all TaskTypes.
	 * 
	 * @return a LinkedList of containing all TaskTypes.
	 */
	public LinkedList<TaskType> getAllTypes(){
		LinkedList<TaskType> taskTypes = new LinkedList<TaskType>(this.optionTypes);
		taskTypes.addAll(this.partTypes);
		return taskTypes;
	}
	
	/**
	 * Return all OptionTypes.
	 * 
	 * @return a LinkedList of containing all OptionTypes.
	 */
	public LinkedList<OptionType> getAllOptionTypes(){
		LinkedList<OptionType> optionTypes = new LinkedList<OptionType>(this.optionTypes);
		return optionTypes;
	}
	
	/**
	 * Return all PartTypes.
	 * 
	 * @return a LinkedList of containing all PartTypes.
	 */
	public LinkedList<PartType> getAllPartTypes(){
		LinkedList<PartType> partTypes = new LinkedList<PartType>(this.partTypes);
		return partTypes;
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

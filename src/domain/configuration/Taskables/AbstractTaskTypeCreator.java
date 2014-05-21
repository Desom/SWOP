package domain.configuration.Taskables;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class AbstractTaskTypeCreator {

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

	/**
	 * Return all TaskTypes.
	 * 
	 * @return a LinkedList of containing all TaskTypes.
	 */
	public abstract LinkedList<TaskType> getAllTypes();

	/**
	 * Return all OptionTypes.
	 * 
	 * @return a LinkedList of containing all OptionTypes.
	 */
	public abstract LinkedList<OptionType> getAllOptionTypes();

	/**
	 * Return all PartTypes.
	 * 
	 * @return a LinkedList of containing all PartTypes.
	 */
	public abstract LinkedList<PartType> getAllPartTypes();

	/**
	 * Returns all option types that are available for single task orders.
	 * 
	 * @return all option types that are available for single task orders.
	 */
	public abstract ArrayList<OptionType> getAllSingleTaskPossibleTypes();

	/**
	 * Returns all option types that are mandatory for vehicle orders.
	 * 
	 * @return all option types that are mandatory for vehicle orders.
	 */
	public abstract ArrayList<OptionType> getAllMandatoryTypes();

	/**
	 * Returns the tasktype that has the specified name
	 * 
	 * @param name the name of the desired taskType
	 * @return the requested tasktype, or null if it does not exist
	 */
	public abstract TaskType getTaskType(String name);

}
package domain.assembly;

import java.util.LinkedList;

import domain.configuration.TaskType;

public class WorkstationType {
	
	private String name;
	private LinkedList<TaskType> acceptedTaskTypes;

	/**
	 * Constructor of WorkstationType.
	 * 
	 * @param name
	 * 		The name of this workstationType.
	 * @param acceptedTaskTypes
	 * 		A list of taskTypes that can be processed on this type of workstation.
	 */
	WorkstationType(String name, LinkedList<TaskType> acceptedTaskTypes){
		this.name = name;
		this.acceptedTaskTypes = new LinkedList<TaskType>(acceptedTaskTypes);
	}
	
	/**
	 * Returns a LinkedList of all task types that can be processed on this type of workstation.
	 * 
	 * @return a LinkedList of all task types that can be processed on this type of workstation.
	 */
	public LinkedList<TaskType> getacceptedTaskTypes(){
		return new LinkedList<TaskType>(acceptedTaskTypes);
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

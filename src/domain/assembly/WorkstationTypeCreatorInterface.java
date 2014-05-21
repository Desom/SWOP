package domain.assembly;

import java.util.LinkedList;

public interface WorkstationTypeCreatorInterface {

	/**
	 * Return all WorkstationTypes.
	 * 
	 * @return a LinkedList of containing all WorkstationTypes.
	 */
	public abstract LinkedList<WorkstationType> getAllWorkstationTypes();

	/**
	 * Returns the workstationtype that has the specified name
	 * 
	 * @param name the name of the desired workstationType
	 * @return the requested workstationtype, or null if it does not exist
	 */
	public abstract WorkstationType getWorkstationType(String name);

}
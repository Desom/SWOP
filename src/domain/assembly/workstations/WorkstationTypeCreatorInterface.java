package domain.assembly.workstations;

import java.util.LinkedList;

public interface WorkstationTypeCreatorInterface {

	/**
	 * Return all WorkstationTypes.
	 * 
	 * @return a LinkedList of containing all WorkstationTypes.
	 */
	public abstract LinkedList<WorkstationType> getAllWorkstationTypes();

	/**
	 * Returns the workstation type that has the specified name.
	 * 
	 * @param name
	 * 		The name of the desired workstation type.
	 * @return The requested workstation type, or null if it does not exist.
	 */
	public abstract WorkstationType getWorkstationType(String name);

}
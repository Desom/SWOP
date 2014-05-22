package domain.assembly.assemblyline.status;

import java.util.ArrayList;

public interface StatusCreatorInterface {

	/**
	 * Returns the operational status object.
	 * 
	 * @return The operational status object.
	 */
	public abstract OperationalStatus getOperationalStatus();

	/**
	 * Returns the maintenance status object.
	 * 
	 * @return The maintenance status object.
	 */
	public abstract MaintenanceStatus getMaintenanceStatus();

	/**
	 * Returns the broken status object.
	 * 
	 * @return The broken status object.
	 */
	public abstract BrokenStatus getBrokenStatus();
	/**
	 * returns all the possible statuses
	 * @return the possible statuses
	 */
	public abstract ArrayList<AssemblyLineStatus> getAllStatuses();

}
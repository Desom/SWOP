package domain.assembly;

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

}
package domain.assembly;

public class StatusCreator {

	private OperationalStatus operational;
	private MaintenanceStatus maintenance;
	private BrokenStatus broken;
	
	/**
	 * Constructor of StatusCreator.
	 * Creates all status objects.
	 */
	public StatusCreator() {
		this.operational = new OperationalStatus(this);
		this.maintenance = new MaintenanceStatus(this);
		this.broken = new BrokenStatus(this);
	}
	
	/**
	 * Returns the operational status object.
	 * 
	 * @return The operational status object.
	 */
	public OperationalStatus getOperationalStatus() {
		return this.operational;
	}
	
	/**
	 * Returns the maintenance status object.
	 * 
	 * @return The maintenance status object.
	 */
	public MaintenanceStatus getMaintenanceStatus() {
		return this.maintenance;
	}
	
	/**
	 * Returns the broken status object.
	 * 
	 * @return The broken status object.
	 */
	public BrokenStatus getBrokenStatus() {
		return this.broken;
	}
}

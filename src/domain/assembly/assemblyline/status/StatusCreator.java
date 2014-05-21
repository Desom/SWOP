package domain.assembly.assemblyline.status;

import java.util.ArrayList;

import domain.assembly.assemblyline.BrokenStatus;

public class StatusCreator implements StatusCreatorInterface {

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
	
	/* (non-Javadoc)
	 * @see domain.assembly.StatusCreatorInterface#getOperationalStatus()
	 */
	@Override
	public OperationalStatus getOperationalStatus() {
		return this.operational;
	}
	
	/* (non-Javadoc)
	 * @see domain.assembly.StatusCreatorInterface#getMaintenanceStatus()
	 */
	@Override
	public MaintenanceStatus getMaintenanceStatus() {
		return this.maintenance;
	}
	
	/* (non-Javadoc)
	 * @see domain.assembly.StatusCreatorInterface#getBrokenStatus()
	 */
	@Override
	public BrokenStatus getBrokenStatus() {
		return this.broken;
	}
	/**
	 * returns an ArrayList containing AssemblyLineStatus of the following subclass: BrokenStatus, MaintenanceStatus, OperationalStatus.
	 * @return an ArrayList containing AssemblyLineStatus of the following subclass: BrokenStatus, MaintenanceStatus, OperationalStatus
	 */
	@Override
	public ArrayList<AssemblyLineStatus> getAllStatuses() {
		ArrayList<AssemblyLineStatus> result = new ArrayList<AssemblyLineStatus>();
		result.add(this.getBrokenStatus());
		result.add(this.getMaintenanceStatus());
		result.add(this.getOperationalStatus());
		return result;
	}
}

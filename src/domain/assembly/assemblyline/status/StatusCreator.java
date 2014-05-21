package domain.assembly.assemblyline.status;

import java.util.ArrayList;

import domain.assembly.assemblyline.BrokenStatus;

public class StatusCreator implements StatusCreatorInterface {

	private OperationalStatus operational;
	private MaintenanceStatus maintenance;
	private BrokenStatus broken;
	private ArrayList<AssemblyLineStatus> statusList = new ArrayList<AssemblyLineStatus>();
	
	/**
	 * Constructor of StatusCreator.
	 * Creates all status objects.
	 */
	public StatusCreator() {
		this.operational = new OperationalStatus(this);
		this.statusList.add(this.operational);
		this.maintenance = new MaintenanceStatus(this);
		this.statusList.add(this.maintenance);
		this.broken = new BrokenStatus(this);
		this.statusList.add(this.broken);
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
	

	@SuppressWarnings("unchecked")
	
	
	/**
	 * Get a list containing every status.
	 * 
	 * @return a list containing all stati.
	 */
	public ArrayList<AssemblyLineStatus> getAllStatuses(){
		return (ArrayList<AssemblyLineStatus>) this.statusList.clone();
	}
}

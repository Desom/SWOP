package domain.assembly.assemblyline.status;

import java.util.ArrayList;

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
	
	@Override
	public OperationalStatus getOperationalStatus() {
		return this.operational;
	}
	
	@Override
	public MaintenanceStatus getMaintenanceStatus() {
		return this.maintenance;
	}
	
	@Override
	public BrokenStatus getBrokenStatus() {
		return this.broken;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<AssemblyLineStatus> getAllStatuses(){
		return (ArrayList<AssemblyLineStatus>) this.statusList.clone();
	}
}

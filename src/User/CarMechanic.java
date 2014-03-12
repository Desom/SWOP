package User;
import java.util.ArrayList;


public class CarMechanic extends User {
	
	/**
	 * Constructor of CarMechanic.
	 * 
	 * @param	id
	 * 			id of the new car mechanic.
	 */
	public CarMechanic(int id) {
		super(id);
		initializeApprovedMethods();
	}
	
	/**
	 * Initializes all methods (represented by strings) that this car mechanic can use.
	 */
	private void initializeApprovedMethods() {
		this.approvedMethods.add("selectWorkstation");
		this.approvedMethods.add("selectWorkstationID");
		this.approvedMethods.add("getAllPendingTasks");
		this.approvedMethods.add("getAllCompletedTasks");
		this.approvedMethods.add("selectTask");
		this.approvedMethods.add("getActiveTaskInformation");
		this.approvedMethods.add("completeTask");
		this.approvedMethods.add("getAllWorkstations");
	}
	
	/**
	 * Checks if this object is a car mechanic.
	 * 
	 * @return	true
	 */
	@Override
	public boolean isCarMechanic() {
		return true;
	}
}

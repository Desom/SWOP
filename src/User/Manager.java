package User;


public class Manager extends User {

	/**
	 * Constructor of Manager.
	 * 
	 * @param	id
	 * 			id of this manager
	 */
	public Manager(int id) {
		super(id);
		initializeApprovedMethods();
	}

	/**
	 * Initializes all methods (represented by strings) that this manager can use.
	 */
	private void initializeApprovedMethods() {
		this.approvedMethods.add("advanceLine");
		this.approvedMethods.add("currentStatus");
		this.approvedMethods.add("futureStatus");
		this.approvedMethods.add("getAllWorkstations");
		this.approvedMethods.add("selectWorkstationID");
		this.approvedMethods.add("getAllTasks");
		this.approvedMethods.add("hasAllTasksCompleted");
		this.approvedMethods.add("getAssemblyStatus");
		this.approvedMethods.add("getAssemblyLine");
		this.approvedMethods.add("getFutureStatus");
		this.approvedMethods.add("addAssemblyTask");
		this.approvedMethods.add("setDeliveredTime");
	}
}

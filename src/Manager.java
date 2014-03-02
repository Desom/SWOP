
public class Manager extends User {

	public Manager(int id) {
		super(id);
		initializeApprovedMethods();
	}

	private void initializeApprovedMethods() {
		this.approvedMethods.add("advanceLine");
		this.approvedMethods.add("hasAllTasksCompleted");
		this.approvedMethods.add("getAssemblyStatus");
		this.approvedMethods.add("getFutureStatus");
	}
}

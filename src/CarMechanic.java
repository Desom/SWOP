import java.util.ArrayList;


public class CarMechanic extends User {
	
	public CarMechanic(int id) {
		super(id);
		initializeApprovedMethods();
	}
	
	public boolean canPerform(String methodName) {
		return this.approvedMethods.contains(methodName);
	}
	
	private void initializeApprovedMethods() {
		this.approvedMethods.add("completeTask");
		this.approvedMethods.add("selectTask");
	}
}

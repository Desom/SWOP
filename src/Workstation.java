import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class WorkStation {

	private int id;
	private ArrayList<String> taskTypes;
	private User carMechanic;
	private Queue<AssemblyTask> pendingTasks;

	public WorkStation(int id, ArrayList<String> taskTypes) {
		this.setId(id);
		this.setTaskTypes(taskTypes);
		this.pendingTasks = new LinkedList<AssemblyTask>();
	}
	
	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}
	
	public ArrayList<String> getTaskTypes() {
		return (ArrayList<String>) taskTypes.clone();
	}

	// Public omdat task type nog gewijzigd zou kunnen worden?
	public void setTaskTypes(ArrayList<String> taskTypes) {
		this.taskTypes = taskTypes;
	}
	
	public Queue<AssemblyTask> getPendingTasks() {
		// clone
		return this.pendingTasks;
	}
	
	public User getCarMechanic() {
		return carMechanic;
	}

	// hernoemen naar set / assign?
	public void addCarMechanic(User carMechanic) throws Exception {
		if (this.carMechanic == null)
			this.carMechanic = carMechanic;
		else
			throw new Exception("There already has been assigned a car mechanic to this workstation");
	}
	
	public void match(User user, AssemblyTask task) throws Exception {
		// check user
		if (this.taskTypes.contains(task.getType()))
			this.pendingTasks.add(task);
		else
			throw new Exception("This assembly task can't be assigned to this workstation");
	}
}

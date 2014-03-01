import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class Workstation {

	private int id;
	private ArrayList<String> taskTypes;
	private User carMechanic;
	private ArrayList<AssemblyTask> allTasks;
	private AssemblyTask activeTask;

	public Workstation(int id, ArrayList<String> taskTypes) {
		this.setId(id);
		this.setTaskTypes(taskTypes);
		this.allTasks = new ArrayList<AssemblyTask>();
		this.activeTask = null;
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
	
	public ArrayList<AssemblyTask> getPendingTasks(User user) {
		// check user
		// TODO clone
		return this.allTasks;
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
		// TODO check user
		if (this.taskTypes.contains(task.getType()))
			this.allTasks.add(task);
		else
			throw new Exception("This assembly task can't be assigned to this workstation");
	}
	
	public void selectTask(User user, AssemblyTask task) throws Exception {
		// TODO check user
		if (this.activeTask == null)
			this.activeTask = task;
		else
			throw new Exception("Another assemly task is still in progress");
	}
	
	public void completeTask(User user) {
		// check user
		this.activeTask.completeTask();
		this.activeTask = null;
	}
	
	public ArrayList<AssemblyTask> getAllPendingTasks(User user) {
		// check user
		ArrayList<AssemblyTask> allPendingTasks = new ArrayList<AssemblyTask>();
		for (AssemblyTask task : this.allTasks)
			if (!task.isCompleted())
				allPendingTasks.add(task);
		return allPendingTasks;
	}
	
	public ArrayList<AssemblyTask> getAllCompletedTasks(User user) {
		// check user
		ArrayList<AssemblyTask> allCompletedTasks = new ArrayList<AssemblyTask>();
		for (AssemblyTask task : this.allTasks)
			if (task.isCompleted())
				allCompletedTasks.add(task);
		return allCompletedTasks;
	}
	
	public ArrayList<AssemblyTask> getAllTasks(User user) {
		// check user
		return this.allTasks;
	}
	
	public boolean allTasksCompleted(User user) {
		// check user
		for (AssemblyTask task : this.allTasks)
			if (!task.isCompleted())
				return false;
		return true;
	}
}

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
	
	// Wie voert deze methode uit?
	public void match(User user, AssemblyTask task) throws Exception {
		this.checkUser(user, "match");
		if (this.taskTypes.contains(task.getType()))
			this.allTasks.add(task);
		else
			throw new Exception("This assembly task can't be assigned to this workstation");
	}
	
	public void selectTask(User user, AssemblyTask task) throws Exception {
		this.checkUser(user, "selectTask");
		if (this.activeTask == null)
			this.activeTask = task;
		else
			throw new Exception("Another assemly task is still in progress");
	}
	
	public void completeTask(User user) throws Exception {
		this.checkUser(user, "completeTask");
		this.activeTask.completeTask();
		this.activeTask = null;
	}
	
	public ArrayList<AssemblyTask> getAllPendingTasks(User user) throws Exception {
		this.checkUser(user, "getAllPendingTasks");
		ArrayList<AssemblyTask> allPendingTasks = new ArrayList<AssemblyTask>();
		for (AssemblyTask task : this.allTasks)
			if (!task.isCompleted())
				allPendingTasks.add(task);
		return (ArrayList<AssemblyTask>) allPendingTasks.clone();
	}
	
	public ArrayList<AssemblyTask> getAllCompletedTasks(User user) throws Exception {
		this.checkUser(user, "getAllCompletedTasks");
		ArrayList<AssemblyTask> allCompletedTasks = new ArrayList<AssemblyTask>();
		for (AssemblyTask task : this.allTasks)
			if (task.isCompleted())
				allCompletedTasks.add(task);
		return (ArrayList<AssemblyTask>) allCompletedTasks.clone();
	}
	
	public ArrayList<AssemblyTask> getAllTasks(User user) throws Exception {
		this.checkUser(user, "getAllTasks");
		return (ArrayList<AssemblyTask>) this.allTasks.clone();
	}
	
	public boolean hasAllTasksCompleted(User user) throws Exception {
		this.checkUser(user, "hasAllTasksCompleted");
		for (AssemblyTask task : this.allTasks)
			if (!task.isCompleted())
				return false;
		return true;
	}
	
	// Niet in klasse diagram, maar nodig in use case
	public ArrayList<String> getActiveTaskInformation(User user) throws Exception {
		this.checkUser(user, "match");
		if (this.activeTask == null)
			throw new Exception("There is no active task at this moment");
		ArrayList<String> information = new ArrayList<String>();
		information.add(this.activeTask.getType());
		for (String action : this.activeTask.getActions())
			information.add(action);
		return information;
	}
	
	private void checkUser(User user, String methodString) throws Exception {
		if (!user.canPerform(methodString))
			throw new Exception("User is not authorized for this action");
	}
}

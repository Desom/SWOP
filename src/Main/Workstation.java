package Main;
import java.util.ArrayList;

// TODO Nieuwe error-types gebruiken als ze worden gepusht

public class Workstation {

	private int id;
	private final ArrayList<String> taskTypes;
	private User carMechanic;
	private ArrayList<AssemblyTask> allTasks;
	private AssemblyTask activeTask;

	/**
	 * Constructor of Workstation.
	 * Creates a new workstation with a specific id and a list of taskTypes.
	 * 
	 * @param id
	 * @param taskTypes
	 */
	public Workstation(int id, ArrayList<String> taskTypes) {
		this.setId(id);
		this.taskTypes = taskTypes;
		this.allTasks = new ArrayList<AssemblyTask>();
		this.activeTask = null;
	}
	
	/**
	 * Getter for the id of this workstation.
	 * 
	 * @return The id of this Workstation.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of this workstation. Only used in constructor.
	 * 
	 * @param	id
	 * 			The id that is given to this Workstation.
	 */
	private void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gives a list of all task types this workstation can perform.
	 * 
	 * @return A copy of all possible types of the tasks this Workstation can perform.
	 */
	public ArrayList<String> getTaskTypes() {
		return (ArrayList<String>) taskTypes.clone();
	}
	
	/**
	 * Gives the car mechanic that is operating at this workstation.
	 * 
	 * @return The CarMechanic operating at this Workstation.
	 */
	public User getCarMechanic() {
		return carMechanic;
	}

	/**
	 * Add a given car mechanic to this workstation.
	 * 
	 * @param	carMechanic
	 * 			The desired car mechanic to operate at this Workstation.
	 * @throws 	Exception
	 * 			If there is already a car mechanic operating at this Workstation.
	 */
	public void addCarMechanic(User carMechanic) throws Exception {
		if (this.carMechanic == null)
			this.carMechanic = carMechanic;
		else
			throw new Exception("There already has been assigned a car mechanic to this workstation");
	}
	
	// TODO welke user is authorized?
	/**
	 * Adds an assembly task to this workstation.
	 * 
	 * @param	user
	 * 			The User that wants to call this method.
	 * @param	task
	 * 			The AssemblyTask that will be added.
	 * @throws	Exception
	 * 			If task does not match with the task types list.
	 * 			If the user is not authorized to call the given method.
	 */
	public void addAssemblyTask(User user, AssemblyTask task) throws Exception {
		this.checkUser(user, "match");
		if (this.taskTypes.contains(task.getType()))
			this.allTasks.add(task);
		else
			throw new Exception("This assembly task can't be assigned to this workstation");
	}
	
	/**
	 * Selects the given task to be the active task of this workstation.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @param	task
	 * 			The task that the user wants to work on.
	 * @throws	Exception
	 * 			If another task is still in progress.
	 * 			If the user is not authorized to call the given method.
	 */
	public void selectTask(User user, AssemblyTask task) throws Exception {
		this.checkUser(user, "selectTask");
		if (this.activeTask == null)
			this.activeTask = task;
		else
			throw new Exception("Another assembly task is still in progress");
	}
	
	/**
	 * Completes the assembly task that the operating car mechanic is working on.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @throws	Exception
	 * 			If the user is not authorized to call the given method.
	 */
	public void completeTask(User user) throws Exception {
		this.checkUser(user, "completeTask");
		this.activeTask.completeTask();
		this.activeTask = null;
	}
	
	/**
	 * Gives a list of all pending assembly tasks.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @return	A copy of the list of all pending assembly tasks.
	 * @throws	Exception
	 * 			If the user is not authorized to call the given method.
	 */
	public ArrayList<AssemblyTask> getAllPendingTasks(User user) throws Exception {
		this.checkUser(user, "getAllPendingTasks");
		ArrayList<AssemblyTask> allPendingTasks = new ArrayList<AssemblyTask>();
		for (AssemblyTask task : this.allTasks)
			if (!task.isCompleted())
				allPendingTasks.add(task);
		return (ArrayList<AssemblyTask>) allPendingTasks.clone();
	}
	
	/**
	 * Gives a list of all completed assembly tasks.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @return	A copy of the list of all completed assembly tasks.
	 * @throws Exception
	 * 			If the user is not authorized to call the given method.
	 */
	public ArrayList<AssemblyTask> getAllCompletedTasks(User user) throws Exception {
		this.checkUser(user, "getAllCompletedTasks");
		ArrayList<AssemblyTask> allCompletedTasks = new ArrayList<AssemblyTask>();
		for (AssemblyTask task : this.allTasks)
			if (task.isCompleted())
				allCompletedTasks.add(task);
		return (ArrayList<AssemblyTask>) allCompletedTasks.clone();
	}
	
	/**
	 * Gives a list of all assembly tasks.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @return	A copy of the list of all assembly tasks.
	 * @throws	Exception
	 * 			If the user is not authorized to call the given method.
	 */
	public ArrayList<AssemblyTask> getAllTasks(User user) throws Exception {
		this.checkUser(user, "getAllTasks");
		return (ArrayList<AssemblyTask>) this.allTasks.clone();
	}
	
	/**
	 * Returns true if all assembly tasks of this workstation are completed, otherwise false.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @return	True if all assembly tasks are completed, otherwise false.
	 * @throws	Exception
	 * 			If the user is not authorized to call the given method.
	 */
	public boolean hasAllTasksCompleted(User user) throws Exception {
		this.checkUser(user, "hasAllTasksCompleted");
		for (AssemblyTask task : this.allTasks)
			if (!task.isCompleted())
				return false;
		return true;
	}
	
	/**
	 * Returns the task type of the active assembly task of this workstation along with the actions needed to complete this task.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @return	A list with on the first line the task type of the active assembly task. The following lines indicate the actions needed to complete this task.
	 * @throws	Exception
	 * 			If there is no active assembly task.
	 * 			If the user is not authorized to call the given method.
	 */
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
	
	/**
	 * Checks if the give user can perform the given method (defined by a string). 
	 * 
	 * @param	user
	 * 			The user that wants to call the given method.
	 * @param	methodString
	 * 			The string that defines the method.
	 * @throws	Exception
	 * 			If the user is not authorized to call the given method.
	 */
	private void checkUser(User user, String methodString) throws Exception {
		if (!user.canPerform(methodString))
			throw new Exception("This user is not authorized for this action");
	}
}

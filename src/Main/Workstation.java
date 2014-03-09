package Main;
import java.util.ArrayList;

public class Workstation {

	private int id;
	private final ArrayList<String> taskTypes; // DEZE MOETEN OVEREEN KOMEN MET ASSEMBLYTASK SETTYPE
	/* mogelijke types
	 * -Body
	 * -Color
	 * -Engine
	 * -GearBox
	 * -Seats
	 * -Airco
	 * -Wheels
	 */
	private User carMechanic;
	private ArrayList<AssemblyTask> allTasks;
	private AssemblyTask activeTask;
	private CarAssemblyProcess currentCar = null;

	/**
	 * Clears the workstation of it's current tasks and other attributes related to it's current car.
	 */
	protected void clearCar(){
		this.allTasks = new ArrayList<AssemblyTask>();
		this.activeTask = null;
		this.currentCar = null;
	}
	
	/**
	 * 
	 * @return The assemblyprocess of the car this workstation is currently working on.
	 */
	public CarAssemblyProcess getCurrentCar() {
		return currentCar;
	}

	/**
	 * 
	 * @param currentCar The assemblyprocess of the car this workstation is currently working on.
	 */
	protected void setCurrentCar(CarAssemblyProcess currentCar) {
		this.currentCar = currentCar;
	}

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
	@SuppressWarnings("unchecked")
	public ArrayList<String> getTaskTypes() {
		return (ArrayList<String>) taskTypes.clone();
	}
	
	/**
	 * Gives the car mechanic that is operating at this workstation.
	 * 
	 * @return	The CarMechanic operating at this Workstation.
	 * @throws	IllegalStateException
	 * 			If there is no car mechanic at this workstation.
	 */
	public User getCarMechanic() throws IllegalStateException{
		if (carMechanic == null)
			throw new IllegalStateException("There is no car mechanic at this workstation");
		return carMechanic;
	}

	/**
	 * Add a given car mechanic to this workstation.
	 * 
	 * @param	carMechanic
	 * 			The desired car mechanic to operate at this Workstation.
	 * @throws 	IllegalStateException
	 * 			If there is already a car mechanic operating at this Workstation.
	 * @throws	IllegalArgumentException
	 * 			If the given user is not a car mechanic.
	 */
	public void addCarMechanic(User carMechanic) throws IllegalStateException, IllegalArgumentException {
		if (this.carMechanic == null)
			if (carMechanic.isCarMechanic())
				this.carMechanic = carMechanic;
			else
				throw new IllegalArgumentException("The given user is no car mechanic");
		else
			throw new IllegalStateException("There already has been assigned a car mechanic to this workstation");
	}
	
	/**
	 * Adds an assembly task to this workstation.
	 * 
	 * @param	user
	 * 			The User that wants to call this method.
	 * @param	task
	 * 			The AssemblyTask that will be added.
	 * @throws	IllegalArgumentException
	 * 			If task does not match with the task types list.
	 * @throws	UserAccessException
	 * 			If the user is not authorized to call the given method.
	 */
	public void addAssemblyTask(User user, AssemblyTask task) throws UserAccessException, IllegalArgumentException {
		this.checkUser(user, "addAssemblyTask");
		if (this.taskTypes.contains(task.getType()))
			this.allTasks.add(task);
		else
			throw new IllegalArgumentException("This assembly task can't be assigned to this workstation");
	}
	
	/**
	 * Selects the given task to be the active task of this workstation.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @param	task
	 * 			The task that the user wants to work on.
	 * @throws	IllegalStateException
	 * 			If another task is still in progress.
	 * @throws	UserAccessException
	 * 			If the user is not authorized to call the given method.
	 * @throws	IllegalArgumentException
	 * 			If the selected task is not a pending task.
	 */
	public void selectTask(User user, AssemblyTask task) throws UserAccessException, IllegalStateException, IllegalArgumentException {
		this.checkUser(user, "selectTask");
		if (this.activeTask == null)
			if (this.getAllPendingTasks(user).contains(task))
				this.activeTask = task;
			else
				throw new IllegalArgumentException("This assembly task is not a pending task");
		else
			throw new IllegalStateException("Another assembly task is still in progress");
	}
	
	/**
	 * Completes the assembly task that the operating car mechanic is working on.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @throws	UserAccessException
	 * 			If the user is not authorized to call the given method.
	 * 			If the user is not operating at this workstation.
	 * @throws	IllegalStateException
	 * 			If there is no active task to complete in this workstation.
	 * 			If there is no car mechanic to complete the active task.
	 */
	public void completeTask(User user) throws UserAccessException, IllegalStateException {
		this.checkUser(user, "completeTask");
		if (this.getCarMechanic().getId() != user.getId())
			throw new UserAccessException("This user is not assigned to this workstation");
		if (this.activeTask != null) {
			if (carMechanic != null) {
				this.activeTask.completeTask();
				this.activeTask = null;
			}
			else {
				throw new IllegalStateException("There is no car mechanic to complete the active task");
			}
		}
		else {
			throw new IllegalStateException("There is no active task in this workstation");
		}
	}
	
	/**
	 * Gives a list of all pending assembly tasks.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @return	A copy of the list of all pending assembly tasks.
	 * @throws	UserAccessException
	 * 			If the user is not authorized to call the given method.
	 */
	public ArrayList<AssemblyTask> getAllPendingTasks(User user) throws UserAccessException {
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
	 * @throws	UserAccessException
	 * 			If the user is not authorized to call the given method.
	 */
	public ArrayList<AssemblyTask> getAllCompletedTasks(User user) throws UserAccessException {
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
	 * @throws	UserAccessException
	 * 			If the user is not authorized to call the given method.
	 */
	public ArrayList<AssemblyTask> getAllTasks(User user) throws UserAccessException {
		this.checkUser(user, "getAllTasks");
		return (ArrayList<AssemblyTask>) this.allTasks.clone();
	}
	
	/**
	 * Returns true if all assembly tasks of this workstation are completed, otherwise false.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @return	True if all assembly tasks are completed, otherwise false.
	 * @throws	UserAccessException
	 * 			If the user is not authorized to call the given method.
	 */
	public boolean hasAllTasksCompleted(User user) throws UserAccessException {
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
	 * @throws	UserAccessException
	 * 			If there is no active assembly task.
	 * @throws	IllegalStateException
	 * 			If the user is not authorized to call the given method.
	 */
	public ArrayList<String> getActiveTaskInformation(User user) throws UserAccessException, IllegalStateException {
		this.checkUser(user, "getActiveTaskInformation");
		if (this.activeTask == null)
			throw new IllegalStateException("There is no active task at this moment");
		return this.activeTask.getTaskInformation();
	}

	/**
	 * Checks if the give user can perform the given method (defined by a string). 
	 * 
	 * @param	user
	 * 			The user that wants to call the given method.
	 * @param	methodString
	 * 			The string that defines the method.
	 * @throws	UserAccessException
	 *			If the user is not authorized to call the given method.
	 */
	private void checkUser(User user, String methodString) throws UserAccessException {
		if (!user.canPerform(methodString))
			throw new UserAccessException(user, methodString);
	}
	
	/**
	 * Returns the name of the workstation.
	 */
	@Override
	public String toString() {
		return "Workstation " + this.id;
	}
}

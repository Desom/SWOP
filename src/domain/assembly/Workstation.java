package domain.assembly;
import java.util.ArrayList;

import domain.InternalFailureException;
import domain.configuration.OptionType;
import domain.user.CarMechanic;

public class Workstation {

	private int id;
	private final ArrayList<OptionType> taskTypes; 
	private CarMechanic carMechanic;
	private ArrayList<AssemblyTask> allTasks;
	private AssemblyTask activeTask;
	private AssemblyLine assemblyLine;
	private int timeSpend;
	private CarAssemblyProcess assemblyProcess;

	/**
	 * Constructor of Workstation.
	 * Creates a new workstation with a specific id and a list of taskTypes.
	 * 
	 * @param	id
	 * 			the id of this workstation
	 * @param	taskTypes
	 * 			the task types that can be handled at this workstation
	 */
	public Workstation(AssemblyLine assemblyLine, int id, ArrayList<OptionType> taskTypes) {
		this.assemblyLine = assemblyLine;
		this.setId(id);
		this.taskTypes = taskTypes;
		this.allTasks = new ArrayList<AssemblyTask>();
		this.activeTask = null;
		this.resetTimeSpend();
	}


	/**
	 * Clears this workstation of tasks and the active task and resets the time spend for the current car. Used by the AssemblyLine object in advanceLine().
	 */
	protected void clear() {
		this.allTasks = new ArrayList<AssemblyTask>();
		this.activeTask = null;
		this.resetTimeSpend();
		setCarAssemblyProcess(null);
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
	public ArrayList<OptionType> getTaskTypes() {
		return (ArrayList<OptionType>) taskTypes.clone();
	}

	/**
	 * Gives the car mechanic that is operating at this workstation.
	 * 
	 * @return	The CarMechanic operating at this Workstation.
	 * @throws	IllegalStateException
	 * 			If there is no car mechanic at this workstation.
	 */
	public CarMechanic getCarMechanic() throws IllegalStateException{
		if (carMechanic == null)
			throw new IllegalStateException("There is no car mechanic at this workstation");
		return carMechanic;
	}

	/**
	 * Add a given car mechanic to this workstation.
	 * 
	 * @param	carMechanic
	 * 			The desired car mechanic to operate at this Workstation.
	 */
	public void addCarMechanic(CarMechanic carMechanic) {
		this.carMechanic = carMechanic;
	}
	
	/**
	 * Removes the current car mechanic from this workstation.
	 *
	 * TODO : mag een CarMechanic weg als er een activeTask is?
	 */
	public void removeCarMechanic() {
		this.carMechanic = null;
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
	 */
	public void addAssemblyTask(AssemblyTask task) throws IllegalArgumentException {
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
	 * @throws	IllegalArgumentException
	 * 			If the selected task is not a pending task.
	 */
	public void selectTask(AssemblyTask task) throws IllegalStateException, IllegalArgumentException {
		if (this.activeTask == null)
			if (this.getAllPendingTasks().contains(task))
				this.activeTask = task;
			else
				throw new IllegalArgumentException("This assembly task is not a pending task");
		else
			throw new IllegalStateException("Another assembly task is still in progress");
	}

	/**
	 * Completes the assembly task that the operating car mechanic is working on.
	 * 
	 * @param	carMechanic
	 * 			The user that wants to call this method.
	 * @param 	timeSpend
	 * 			The amount of minutes it took to complete the current activeTask.
	 * @throws	IllegalStateException
	 * 			If there is no active task to complete in this workstation.
	 * 			If there is no car mechanic to complete the active task.
	 * @throws InternalFailureException 
	 * 			If a fatal error occurred the program could not recover from.
	 */
	public void completeTask(CarMechanic carMechanic, int timeSpend) throws IllegalStateException, InternalFailureException {
		if (this.getCarMechanic().getId() != carMechanic.getId())
			throw new IllegalStateException("This user is not assigned to this workstation");
		//TODO is dit echt IllegalStateException? mss IllegalArgument fzo?
		if (this.activeTask != null) {
			if (this.carMechanic != null) {
				this.activeTask.completeTask(timeSpend);
				this.activeTask = null;
			}
			else {
				throw new IllegalStateException("There is no car mechanic to complete the active task");
			}
		}
		else {
			throw new IllegalStateException("There is no active task in this workstation");
		}
		this.addTimeSpend(timeSpend);
		try {
			this.assemblyLine.advanceLine();
		} catch (CannotAdvanceException e) {
		}
	}

	public int getTimeSpend() {
		return timeSpend;
	}


	private void addTimeSpend(int timeSpend) {
		this.timeSpend += timeSpend;
		
	}
	
	private void resetTimeSpend() {
		this.timeSpend = 0;	
	}
	
	/**
	 * Gives a list of all pending assembly tasks.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @return	A copy of the list of all pending assembly tasks.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<AssemblyTask> getAllPendingTasks(){
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
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<AssemblyTask> getAllCompletedTasks(){
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
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<AssemblyTask> getAllTasks(){
		return (ArrayList<AssemblyTask>) this.allTasks.clone();
	}

	/**
	 * Returns true if all assembly tasks of this workstation are completed, otherwise false.
	 * 
	 * @param	user
	 * 			The user that wants to call this method.
	 * @return	True if all assembly tasks are completed, otherwise false.
	 */
	public boolean hasAllTasksCompleted(){
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
	 * @throws	IllegalStateException
	 * 			If the user is not authorized to call the given method.
	 */
	public ArrayList<String> getActiveTaskInformation() throws IllegalStateException {
		if (this.activeTask == null)
			throw new IllegalStateException("There is no active task at this moment");
		return this.activeTask.getTaskInformation();
	}


	/**
	 * Returns the name of the workstation.
	 */
	@Override
	public String toString() {
		return "Workstation " + this.id;
	}
	
	/**
	 * Set the car assembly process this workstation is currently working on.
	 * 
	 * @param process the specified car assembly process.
	 */
	public void setCarAssemblyProcess(CarAssemblyProcess process){
		this.assemblyProcess = process;
	}
	
	/**
	 * Get the car assembly process this workstation is currently working on.
	 * 
	 * @return the car assembly process this workstation is currently working on.
	 */
	public CarAssemblyProcess getCarAssemblyProcess(){
		return this.assemblyProcess;
	}
}

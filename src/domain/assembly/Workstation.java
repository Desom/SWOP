package domain.assembly;

import java.util.ArrayList;

import domain.InternalFailureException;
import domain.configuration.OptionType;
import domain.user.CarMechanic;

public class Workstation {

	private String name;
	private final ArrayList<OptionType> taskTypes; 
	private CarMechanic carMechanic;
	private ArrayList<AssemblyTask> allTasks;
	private AssemblyTask activeTask;
	private AssemblyLine assemblyLine;
	private int timeSpend;
	private CarAssemblyProcess assemblyProcess;

	/**
	 * Constructor of Workstation.
	 * Creates a new workstation with an assembly line, specific id and a list of taskTypes.
	 * 
	 * @param assemblyLine
	 * 		The assembly line of which this workstation is a part of.
	 * @param name
	 * 		The name of this workstation
	 * @param taskTypes
	 * 		The task types that can be handled at this workstation.
	 */
	public Workstation(AssemblyLine assemblyLine, String name, ArrayList<OptionType> taskTypes) {
		this.assemblyLine = assemblyLine;
		this.name = name;
		this.taskTypes = taskTypes;
		this.allTasks = new ArrayList<AssemblyTask>();
		this.activeTask = null;
		this.resetTimeSpend();
	}


	/**
	 * Clears this workstation of tasks and the active task and resets the time spend for the current car.
	 * Used by the AssemblyLine object in advanceLine().
	 */
	protected void clear() {
		this.allTasks = new ArrayList<AssemblyTask>();
		this.activeTask = null;
		this.resetTimeSpend();
		setCarAssemblyProcess(null);
	}

	/**
	 * Getter for the name of this workstation.
	 * 
	 * @return The name of this Workstation.
	 */
	public String getName() {
		return this.name;
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
	 * @return The CarMechanic operating at this Workstation.
	 * @throws IllegalStateException
	 * 		If there is no car mechanic at this workstation.
	 */
	public CarMechanic getCarMechanic() throws IllegalStateException{
		if (carMechanic == null)
			throw new IllegalStateException("There is no car mechanic at this workstation");
		return carMechanic;
	}

	/**
	 * Add a given car mechanic to this workstation.
	 * 
	 * @param carMechanic
	 * 		The desired car mechanic to operate at this Workstation.
	 */
	public void addCarMechanic(CarMechanic carMechanic) {
		this.carMechanic = carMechanic;
	}
	
	/**
	 * Removes the current car mechanic from this workstation.
	 * 
	 * @throws IllegalStateException
	 * 		If there is an active task in this workstation.
	 */
	public void removeCarMechanic() {
		if(activeTask != null){
			throw new IllegalStateException("A car mechanic cannot leave when there is an active task");
		}
		this.carMechanic = null;
	}

	/**
	 * Adds an assembly task to this workstation.
	 * 
	 * @param task
	 * 		The AssemblyTask that will be added.
	 * @throws IllegalArgumentException
	 * 		If task does not match with the task types list.
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
	 * @param task
	 * 		The task that the user wants to work on.
	 * @throws IllegalStateException
	 * 		If another task is still in progress.
	 * @throws IllegalArgumentException
	 * 		If the selected task is not a pending task.
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
	 * @param carMechanic
	 * 		The user that wants to call this method.
	 * @param timeSpent
	 * 		The amount of minutes it took to complete the current activeTask.
	 * @throws IllegalArgumentException
	 * 		If there is no active task to complete in this workstation.
	 * 		If there is no car mechanic to complete the active task.
	 */
	public void completeTask(CarMechanic carMechanic, int timeSpent) throws IllegalStateException {
		if (this.getCarMechanic().getId() != carMechanic.getId())
			throw new IllegalArgumentException("This user is not assigned to this workstation");
		if (this.activeTask != null) {
			if (this.carMechanic != null) {
				this.activeTask.completeTask(timeSpent);
				this.activeTask = null;
			}
			else {
				throw new IllegalStateException("There is no car mechanic to complete the active task");
			}
		}
		else {
			throw new IllegalStateException("There is no active task in this workstation");
		}
		this.addTimeSpend(timeSpent);
		if(this.assemblyLine.canAdvanceLine()){
			try {
				this.assemblyLine.advanceLine();
			}
			catch (CannotAdvanceException e) {
				throw new InternalFailureException("The AssemblyLine couldn't advance even though canAdvanceLine() returned true.");
			}
		}
	}

	/**
	 * Gets the time already spent on the current assembly process.
	 * 
	 * @return the time already spent on the current assembly process
	 */
	public int getTimeSpend() {
		return timeSpend;
	}


	/**
	 * Adds time to the time already spent on the current assembly process.
	 * 
	 * @param timeSpend
	 * 		The time to be added to the time already spent on the current assembly process.
	 */
	private void addTimeSpend(int timeSpend) {
		this.timeSpend += timeSpend;
		
	}
	
	/**
	 * Resets the time already spent on the current assembly process.
	 */
	private void resetTimeSpend() {
		this.timeSpend = 0;	
	}
	
	/**
	 * Gives a list of all pending assembly tasks.
	 * 
	 * @return A copy of the list of all pending assembly tasks.
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
	 * @return A copy of the list of all completed assembly tasks.
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
	 * @return A copy of the list of all assembly tasks.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<AssemblyTask> getAllTasks(){
		return (ArrayList<AssemblyTask>) this.allTasks.clone();
	}

	/**
	 * Returns true if all assembly tasks of this workstation are completed, otherwise false.
	 * 
	 * @return True if all assembly tasks are completed, otherwise false.
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
	 * @return A list with on the first line the task type of the active assembly task. The following lines indicate the actions needed to complete this task.
	 * @throws IllegalStateException
	 * 		If there is no active task.
	 */
	public ArrayList<String> getActiveTaskInformation() throws IllegalStateException {
		if (this.activeTask == null)
			throw new IllegalStateException("There is no active task at this moment");
		return this.activeTask.getTaskInformation();
	}


	/**
	 * Returns the string which represents this workstation.
	 */
	@Override
	public String toString() {
		return "Workstation: " + this.name;
	}
	
	/**
	 * Sets the car assembly process this workstation is currently working on.
	 * Also sets all tasks in the car assembly process compatible with the workstation.
	 * 
	 * @param process
	 * 		the specified car assembly process
	 */
	public void setCarAssemblyProcess(CarAssemblyProcess process){
		if (process != null)
			for (AssemblyTask task : this.compatibleWith(process))
				this.addAssemblyTask(task);
		this.assemblyProcess = process;
	}
	
	/**
	 * Gets the car assembly process this workstation is currently working on.
	 * 
	 * @return the car assembly process this workstation is currently working on.
	 */
	public CarAssemblyProcess getCarAssemblyProcess(){
		return this.assemblyProcess;
	}
	
	/**
	 * Returns the tasks of the given assembly process that are compatible with this workstation.
	 * 
	 * @param assemblyProcess
	 * 		The assembly process to check against.
	 * @return All assembly tasks of the car assembly process on which can be worked on in this workstation.
	 */
	protected ArrayList<AssemblyTask> compatibleWith(CarAssemblyProcess assemblyProcess){
		ArrayList<OptionType> acceptedTypes = this.getTaskTypes();
		ArrayList<AssemblyTask> compatibleTasks = new ArrayList<AssemblyTask>();
		for(AssemblyTask task : assemblyProcess.getAssemblyTasks()){
			if(acceptedTypes.contains(task.getType())){
				compatibleTasks.add(task);
			}
		}
		return compatibleTasks;
	}
}

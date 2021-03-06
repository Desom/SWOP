package domain.assembly.workstations;

import java.util.ArrayList;
import java.util.LinkedList;

import domain.configuration.OptionType;
import domain.configuration.TaskType;
import domain.user.Mechanic;

public class Workstation {

	private String name;
	private final WorkstationType workstationType;
	private Mechanic mechanic;
	private ArrayList<AssemblyTask> allTasks;
	private AssemblyTask activeTask;
	private ArrayList<WorkstationObserver> observers;
	private int timeSpent;
	private VehicleAssemblyProcess assemblyProcess;

	/**
	 * Constructor of Workstation.
	 * 
	 * @param name
	 * 		The name of this workstation.
	 * @param workstationType
	 * 		The type of this workstation.
	 */
	public Workstation(String name, WorkstationType workstationType) {
		this.observers = new ArrayList<WorkstationObserver>();
		this.name = name;
		this.workstationType = workstationType;
		this.allTasks = new ArrayList<AssemblyTask>();
		this.activeTask = null;
		this.resetTimeSpent();
	}

	/**
	 * Clears this workstation of tasks and the active task and resets the time spend for the current vehicle.
	 */
	public void clear() {
		this.allTasks = new ArrayList<AssemblyTask>();
		this.activeTask = null;
		this.resetTimeSpent();
		setVehicleAssemblyProcess(null);
	}

	/**
	 * Returns the name of this workstation.
	 * 
	 * @return The name of this workstation.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns a list of all task types this workstation can perform.
	 * 
	 * @return A copy of all possible types of the tasks this workstation can perform.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<TaskType> getTaskTypes() {
		return new ArrayList<TaskType>(((LinkedList<OptionType>) workstationType.getacceptedTaskTypes().clone()));
	}
	
	/**
	 * Returns the type of this workstation.
	 * 
	 * @return The type of this workstation.
	 */
	public WorkstationType getWorkstationType() {
		return workstationType;
	}

	/**
	 * Returns the mechanic that is operating at this workstation.
	 * 
	 * @return The mechanic operating at this Workstation.
	 * @throws IllegalStateException
	 * 		If there is no mechanic at this workstation.
	 */
	public Mechanic getMechanic() throws IllegalStateException{
		if (mechanic == null)
			throw new IllegalStateException("There is no mechanic at this workstation");
		return mechanic;
	}

	/**
	 * Add a given mechanic to this workstation.
	 * 
	 * @param mechanic
	 * 		The desired mechanic to operate at this Workstation.
	 */
	public void addMechanic(Mechanic mechanic) {
		this.mechanic = mechanic;
	}
	
	/**
	 * Removes the current mechanic from this workstation.
	 * 
	 * @throws IllegalStateException
	 * 		If there is an active task in this workstation.
	 */
	public void removeMechanic() {
		if(activeTask != null){
			throw new IllegalStateException("A mechanic cannot leave when there is an active task");
		}
		this.mechanic = null;
	}

	/**
	 * Adds an assembly task to this workstation.
	 * 
	 * @param task
	 * 		The AssemblyTask that will be added.
	 * @throws IllegalArgumentException
	 * 		If this workstation can't handle the given task.
	 */
	protected void addAssemblyTask(AssemblyTask task) throws IllegalArgumentException {
		if (this.workstationType.getacceptedTaskTypes().contains(task.getType()))
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
	 * Completes the assembly task that the operating mechanic is working on.
	 * 
	 * @param mechanic
	 * 		The user that wants to call this method.
	 * @param timeSpent
	 * 		The amount of minutes it took to complete the current activeTask.
	 * @throws IllegalArgumentException
	 * 		If there is no active task to complete in this workstation.
	 * 		If there is no mechanic to complete the active task.
	 */
	public void completeTask(Mechanic mechanic, int timeSpent) throws IllegalStateException {
		if (this.getMechanic().getId() != mechanic.getId())
			throw new IllegalArgumentException("This user is not assigned to this workstation");
		if (this.activeTask != null) {
			if (this.mechanic != null) {
				this.activeTask.completeTask(timeSpent);
				this.activeTask = null;
			}
			else {
				throw new IllegalStateException("There is no mechanic to complete the active task");
			}
		}
		else {
			throw new IllegalStateException("There is no active task in this workstation");
		}
		this.addTimeSpent(timeSpent);
		this.notifyObservers();
	}

	/**
	 * Notifies all observers.
	 */
	private void notifyObservers() {
		for(WorkstationObserver observer : this.observers){
			observer.update();
		}
	}

	/**
	 * Returns the time already spent on the current assembly process.
	 * 
	 * @return The time already spent on the current assembly process
	 */
	public int getTimeSpent() {
		return timeSpent;
	}

	/**
	 * Adds time to the time already spent on the current assembly process.
	 * 
	 * @param timeSpent
	 * 		The time to be added to the time already spent on the current assembly process.
	 */
	private void addTimeSpent(int timeSpent) {
		this.timeSpent += timeSpent;
		
	}
	
	/**
	 * Resets the time already spent on the current assembly process.
	 */
	private void resetTimeSpent() {
		this.timeSpent = 0;	
	}
	
	/**
	 * Returns a list of all pending assembly tasks.
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
	 * Returns a list of all completed assembly tasks.
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
	 * Returns a list of all assembly tasks.
	 * 
	 * @return A copy of the list of all assembly tasks.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<AssemblyTask> getAllTasks(){
		return (ArrayList<AssemblyTask>) this.allTasks.clone();
	}

	/**
	 * Checks if all assembly tasks of this workstation are completed.
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
	 * Sets the vehicle assembly process this workstation is currently working on.
	 * Also sets all tasks in the vehicle assembly process compatible with the workstation.
	 * 
	 * @param process
	 * 		The vehicle assembly process to be set.
	 */
	public void setVehicleAssemblyProcess(VehicleAssemblyProcess process){
		if (process != null)
			for (AssemblyTask task : this.compatibleWith(process))
				this.addAssemblyTask(task);
		this.assemblyProcess = process;
	}
	
	/**
	 * Gets the vehicle assembly process this workstation is currently working on.
	 * 
	 * @return The vehicle assembly process this workstation is currently working on.
	 */
	public VehicleAssemblyProcess getVehicleAssemblyProcess(){
		return this.assemblyProcess;
	}
	
	/**
	 * Returns the tasks of the given assembly process that are compatible with this workstation.
	 * 
	 * @param assemblyProcess
	 * 		The assembly process to check against.
	 * @return All assembly tasks of the vehicle assembly process on which can be worked on in this workstation.
	 */
	public ArrayList<AssemblyTask> compatibleWith(VehicleAssemblyProcess assemblyProcess){
		ArrayList<TaskType> acceptedTypes = this.getTaskTypes();
		ArrayList<AssemblyTask> compatibleTasks = new ArrayList<AssemblyTask>();
		for(AssemblyTask task : assemblyProcess.getAssemblyTasks()){
			if(acceptedTypes.contains(task.getType())){
				compatibleTasks.add(task);
			}
		}
		return compatibleTasks;
	}
	
	/**
	 * Adds an observer for this workstation.
	 * 
	 * @param observer
	 * 		The new observer for this workstation.
	 */
	public void addObserver(WorkstationObserver observer){
		this.observers.add(observer);
	}
}

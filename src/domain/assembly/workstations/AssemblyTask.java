package domain.assembly.workstations;

import java.util.ArrayList;

import domain.configuration.TaskType;
import domain.configuration.Taskable;

/**
 * In this class can't exist any methods with public visibility which can harm the integrity of its objects.
 * Objects of this class may be given to the UI.
 */
public class AssemblyTask {
	
	private ArrayList<String> actions;
	private final Taskable taskable;
	private boolean isCompleted;
	private final VehicleAssemblyProcess assemblyProcess;

	/**
	 * Constructor of AssemblyTask.
	 * The created assembly task is not completed by default.
	 * 
	 * @param actions
	 * 		A list of actions that is needed to complete this AssemblyTask
	 * @param taskable
	 * 		The taskable object of this assembly task.
	 * @param assemblyProcess
	 * 		The assembly process to which this assembly task belongs.
	 */
	public AssemblyTask(ArrayList<String> actions, Taskable taskable, VehicleAssemblyProcess assemblyProcess) {
		this(actions, taskable, false, assemblyProcess);
	}
	
	/**
	 * Constructor of AssemblyTask.
	 * 
	 * @param actions
	 * 		A list of actions that is needed to complete this AssemblyTask.
	 * @param taskable
	 * 		The taskable object of this assembly task.
	 * @param isCompleted
	 * 		Indicates whether this assembly task is completed or not.
	 * @param assemblyProcess
	 * 		The assembly process to which this assembly task belongs.
	 */
	public AssemblyTask(ArrayList<String> actions, Taskable taskable, boolean isCompleted, VehicleAssemblyProcess assemblyProcess) {
		this.setActions(actions);
		this.taskable = taskable;
		this.isCompleted = isCompleted;
		this.assemblyProcess = assemblyProcess;
	}
	
	/**
	 * Gives the type of this assembly task.
	 * 
	 * @return	The taskType of this assemblyTask.
	 */
	public TaskType getType() {
		return taskable.getType();
	}
	
	/**
	 * Gives the Option or Part (Taskable) corresponding to this type.
	 * 
	 * @return The Option or Part (Taskable) corresponding to this type.
	 */
	public Taskable getTaskable() {
		return taskable;
	}
	
	/**
	 * Returns a list of the actions needed to complete this assembly task.
	 * 
	 * @return A copy of the list of all actions needed to complete this AssemblyTask.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getActions() {
		return (ArrayList<String>) actions.clone();
	}

	/**
	 * Sets the needed actions to complete this assembly task. Only used in constructor.
	 * 
	 * @param actions
	 * 		The list of actions needed to complete this AssemblyTask.
	 */
	private void setActions(ArrayList<String> actions) {
		this.actions = actions;
	}
	
	/**
	 * Returns true if this assembly task is completed, otherwise false.
	 * 
	 * @return True if this AssemblyTask is completed, otherwise false.
	 */
	public boolean isCompleted() {
		return this.isCompleted;
	}
	
	/**
	 * Completes this assembly task and adds the amount of time used to do so to the configuration this task belongs to.
	 * 
	 * @param minutes
	 * 		The amount of time (in minutes) spent working on completing this assemblyTask.
	 * 		
	 */
	protected void completeTask(int minutes) {
		this.isCompleted = true;
		this.assemblyProcess.addTimeWorked(minutes);
	}
	
	/**
	 * Returns the task type of this assembly task along with the actions needed to complete this task.
	 * 
	 * @return A list with on the first line the task type of this assembly task. The following lines indicate the actions needed to complete this task.
	 */
	public ArrayList<String> getTaskInformation() {
		ArrayList<String> information = new ArrayList<String>();
		information.add(this.getType().toString());
		for (String action : this.getActions())
			information.add(action);
		return information;
	}
	
	/**
	 * Returns the string representation of this assembly task.
	 */
	@Override
	public String toString() {
		String name = this.taskable.getDescription() + ":";
		for (String action : this.actions)
			name += " " + action;
		return name;
	}
}
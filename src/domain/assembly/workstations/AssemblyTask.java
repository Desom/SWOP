package domain.assembly.workstations;

import java.util.ArrayList;

import domain.configuration.Taskables.TaskType;
import domain.configuration.Taskables.Taskable;

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
	 * Creates an assembly task with a list of actions that are needed to complete this assembly task and the type of this assembly task.
	 * The created assembly task is not completed by default.
	 * 
	 * @param	actions
	 * 			A list of actions that is needed to complete this AssemblyTask
	 * @param	type
	 * 			An enum object that indicates the type of this AssemblyTask
	 */
	public AssemblyTask(ArrayList<String> actions, Taskable taskable, VehicleAssemblyProcess process) {
		this(actions, taskable, false, process);
	}
	
	/**
	 * Constructor of AssemblyTask.
	 * Creates an assembly task with a list of actions that are needed to complete this assembly task, the type of this assembly task and a boolean indicating if it is already completed or not.
	 * 
	 * @param	actions
	 * 			A list of actions that is needed to complete this AssemblyTask.
	 * @param	type
	 * 			An enum object that indicates the type of this AssemblyTask.
	 * @param	isCompleted
	 * 			A boolean telling if this assembly task is already completed or not
	 */
	public AssemblyTask(ArrayList<String> actions, Taskable taskable, boolean isCompleted, VehicleAssemblyProcess process) {
		this.setActions(actions);
		this.taskable = taskable;
		this.isCompleted = isCompleted;
		this.assemblyProcess = process;
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
	 * @return	the Option or Part (Taskable) corresponding to this type.
	 */
	public Taskable getTaskable() {
		return taskable;
	}
	
	
	/**
	 * Gives a list of the actions needed to complete this assembly task.
	 * 
	 * @return	A copy of the list of all actions needed to complete this AssemblyTask.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getActions() {
		return (ArrayList<String>) actions.clone();
	}

	/**
	 * Sets the needed actions to complete this assembly task. Only used in constructor.
	 * 
	 * @param	actions
	 * 			The list of actions needed to complete this AssemblyTask.
	 */
	private void setActions(ArrayList<String> actions) {
		this.actions = actions;
	}
	
	/**
	 * Returns true if this assembly task is completed, otherwise false.
	 * 
	 * @return	True if this AssemblyTask is completed, otherwise false.
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
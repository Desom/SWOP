package domain.assembly;

import java.util.ArrayList;

import domain.configuration.OptionType;

public class AssemblyTask {
	
	private ArrayList<String> actions;
	private OptionType type;
	private boolean isCompleted;

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
	public AssemblyTask(ArrayList<String> actions, OptionType type) {
		this(actions, type, false);
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
	public AssemblyTask(ArrayList<String> actions, OptionType type, boolean isCompleted) {
		this.setActions(actions);
		this.setType(type);
		this.isCompleted = isCompleted;
	}
	
	/**
	 * Gives the type of this assembly task.
	 * 
	 * @return	The type of this AssemblyTask indicated by an enum object.
	 */
	public OptionType getType() {
		return type;
	}

	/**
	 * Sets the type of this assembly task. Only used in constructor.
	 * 
	 * @param	type
	 * 			The enum object that indicates the type of this AssemblyTask.
	 */
	private void setType(OptionType type) { // DEZE MOETEN OVEREEN KOMEN MET WORKSTATION
		this.type = type;
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
	 * Completes this assembly task.
	 */
	protected void completeTask() {
		this.isCompleted = true;
	}
	
	/**
	 * Returns the task type of this assembly task along with the actions needed to complete this task.
	 * 
	 * @return	A list with on the first line the task type of this assembly task. The following lines indicate the actions needed to complete this task.
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
		String name = this.type + ":";
		for (String action : this.actions)
			name += " " + action;
		return name;
	}
}
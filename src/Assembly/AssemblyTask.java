package Assembly;

import java.util.ArrayList;

public class AssemblyTask {

	private ArrayList<String> actions;
	private String type;
	private boolean isCompleted;

	/**
	 * Constructor of AssemblyTask.
	 * Creates an assembly task with a list of actions that are needed to complete this assembly task and the type of this assembly task.
	 * 
	 * @param	actions
	 * 			A list of actions that is needed to complete this AssemblyTask.
	 * @param	type
	 * 			A string that indicates the type of this AssemblyTask.
	 */
	public AssemblyTask(ArrayList<String> actions, String type) {
		this.setActions(actions);
		this.setType(type);
		isCompleted = false;
	}
	
	/**
	 * Gives the type of this assembly task.
	 * 
	 * @return	The type of this AssemblyTask.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of this assembly task. Only used in constructor.
	 * 
	 * @param	type
	 * 			The string that indicates the type of this AssemblyTask.
	 */
	private void setType(String type) { // DEZE MOETEN OVEREEN KOMEN MET WORKSTATION
		this.type = type;
	}
	
	/**
	 * Gives a list of the actions needed to complete this assembly task.
	 * 
	 * @return	A copy of the list of all actions needed to complete this AssemblyTask.
	 */
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
	public void completeTask() {
		this.isCompleted = true;
	}
	
	/**
	 * Returns the task type of this assembly task along with the actions needed to complete this task.
	 * 
	 * @return	A list with on the first line the task type of this assembly task. The following lines indicate the actions needed to complete this task.
	 */
	public ArrayList<String> getTaskInformation() {
		ArrayList<String> information = new ArrayList<String>();
		information.add(this.getType());
		for (String action : this.getActions())
			information.add(action);
		return information;
	}
}
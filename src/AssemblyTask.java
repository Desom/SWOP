
public class AssemblyTask {

	private String[] actions;
	private String type;
	private boolean isCompleted;

	public AssemblyTask(String[] actions, String type) {
		this.setActions(actions);
		this.setType(type);
		isCompleted = false;
	}
	
	public String getType() {
		return type;
	}

	private void setType(String type) {
		this.type = type;
	}
	
	private String[] getActions() {
		return actions.clone();
	}

	private void setActions(String[] actions) {
		this.actions = actions;
	}
	
	public boolean isCompleted() {
		return this.isCompleted;
	}
	
	public void completeTask() {
		this.isCompleted = true;
	}
}
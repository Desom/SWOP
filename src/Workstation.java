
public class Workstation {

	private int id;
	private AssemblyTask[] taskTypes;
	
	public Workstation(int id, AssemblyTask[] taskTypes) {
		this.setId(id);
	}
	
	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}
	
	public AssemblyTask[] getTaskTypes() {
		return taskTypes;
	}

	public void setTaskTypes(AssemblyTask[] taskTypes) {
		this.taskTypes = taskTypes;
	}
}

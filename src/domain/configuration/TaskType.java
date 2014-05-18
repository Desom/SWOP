package domain.configuration;

public class TaskType {
	
	protected String name;
	protected boolean singleTaskPossible;
	protected boolean mandatory;

	/**
	 * Returns whether the tasktype is available for single task orders.
	 * 
	 * @return true if the tasktype is available for single task orders, otherwise false.
	 */
	public boolean isSingleTaskPossible() {
		return singleTaskPossible;
	}
	
	/**
	 * Returns whether the taskstype is mandatory for vehicle orders.
	 * 
	 * @return true if the tasktype is mandatory for vehicle orders, otherwise false.
	 */
	public boolean isMandatory() {
		return mandatory;
	}
	
	/**
	 * Returns the name of this tasktype.
	 * 
	 * @return the name of this tasktype.
	 */
	public String getName(){
		return this.name;
	}
}

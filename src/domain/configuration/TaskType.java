package domain.configuration;

public interface TaskType {
	
	/**
	 * Returns the name of this task type.
	 * 
	 * @return the name of this task type.
	 */
	public String getName();
	
	@Override
	public String toString();
}

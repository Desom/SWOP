package domain.configuration.taskables;

public interface Taskable {

	/**
	 * Returns the description of this part.
	 * 
	 * @return the description of this part
	 */
	public abstract String getDescription();

	/**
	 * Returns the type of this part.
	 * 
	 * @return the type of this part
	 */
	public abstract TaskType getType();

}
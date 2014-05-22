package domain.scheduling.schedulers;

public interface AssemblyLineSchedulerObserver {
	
	/**
	 * React to a change in the assembly line scheduler.
	 */
	public void update();
}

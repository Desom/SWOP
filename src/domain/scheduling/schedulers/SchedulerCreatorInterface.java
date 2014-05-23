package domain.scheduling.schedulers;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public interface SchedulerCreatorInterface {

	/**
	 * Creates a new assembly line scheduler.
	 * 
	 * @param startTime
	 * 		The starting time of the new assembly line scheduler.
	 * @return The new assembly line scheduler.
	 */
	public abstract AssemblyLineScheduler createAssemblyLineScheduler(GregorianCalendar startTime);

	/**
	 * Creates a new factory scheduler.
	 * 
	 * @param alsList
	 * 		A list of assembly line schedulers for the new factory scheduler.
	 * @return The new factory scheduler.
	 */
	public abstract FactoryScheduler createFactoryScheduler(ArrayList<AssemblyLineScheduler> alsList);

}
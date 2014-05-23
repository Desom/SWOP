package domain.scheduling.schedulers;

import java.util.ArrayList;

import domain.scheduling.order.Order;

public interface OrderHandler {
	
	/**
	 * Returns the orders it wants the given scheduler to schedule.
	 * 
	 * @param scheduler
	 * 		The Scheduler that will schedule the returned Orders.
	 * @return The Orders which have to be scheduled by the given scheduler.
	 * @throws IllegalArgumentException
	 * 		If the given scheduler isn't in the list of controlled schedulers.
	 */
	public ArrayList<Order> getOrdersFor(Scheduler scheduler);

	/**
	 * Checks if this OrderHandler has scheduler the given scheduler.
	 * 
	 * @param scheduler
	 * 		The scheduler that will be checked.
	 * @return True the given scheduler is used by the order handler, otherwise false.
	 */
	//TODO betere doc nodig. (ook bij implementing classes)
	public boolean hasScheduler(Scheduler scheduler);
}

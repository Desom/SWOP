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
	 */
	//TODO wat als scheduler niet hoort bij deze orderhandler? null,exception,lege lijst??? Zie OrderManager,FactoryScheduler
	public ArrayList<Order> getOrdersFor(Scheduler scheduler);

	/**
	 * Checks if this OrderHandler has scheduler?? 
	 * 
	 * @param scheduler
	 * 		The Scheduler that will be checked.
	 * @return True if it scheduler is used by the OrderHandler?
	 */
	//TODO betere doc nodig. (ook bij implementing classes)
	public boolean hasScheduler(Scheduler scheduler);
}

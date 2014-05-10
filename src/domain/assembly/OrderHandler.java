package domain.assembly;

import java.util.ArrayList;

import domain.order.Order;

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

	//TODO
	public boolean hasScheduler(Scheduler scheduler);
}

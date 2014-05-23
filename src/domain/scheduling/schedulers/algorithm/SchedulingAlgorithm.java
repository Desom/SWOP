package domain.scheduling.schedulers.algorithm;

import java.util.ArrayList;

import domain.scheduling.order.Order;
import domain.scheduling.schedulers.Scheduler;

public interface SchedulingAlgorithm {

	/**
	 * Schedules the given list of orders and returns it.
	 * 
	 * @param orderList
	 * 		List of orders to be scheduled.
	 * @param scheduler
	 * 		The scheduler.
	 * @return A scheduled version of the given list of orders.
	 */
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList, Scheduler scheduler);
}

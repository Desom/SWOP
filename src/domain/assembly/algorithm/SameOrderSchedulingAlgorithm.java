package domain.assembly.algorithm;

import java.util.ArrayList;

import domain.assembly.Scheduler;
import domain.order.Order;

public class SameOrderSchedulingAlgorithm implements SchedulingAlgorithm {

	/**
	 * Returns the orders in the same order as in orderList.
	 * 
	 * @param orderList
	 * 		List of orders to be scheduled.
	 * @param scheduler
	 * 		The scheduler.
	 * @return A new list with the same order.
	 */
	@Override
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList,
			Scheduler scheduler) {
		return new ArrayList<Order>(orderList);
	}

}
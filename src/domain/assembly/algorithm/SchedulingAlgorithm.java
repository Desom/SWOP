package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.scheduling.order.Order;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.ScheduledOrder;
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

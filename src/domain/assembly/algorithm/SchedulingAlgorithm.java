package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.order.Order;

public interface SchedulingAlgorithm {

	/**
	 * Schedules the given list of orders and returns it.
	 * 
	 * @param orderList
	 * 		List of orders to be scheduled.
	 * @param assemblyLineScheduler
	 * 		The scheduler of the assembly line.
	 * @return A scheduled version of the given list of orders.
	 */
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList, AssemblyLineScheduler assemblyLineScheduler);
	
	/**
	 * Schedules the given list of orders and returns a scheduled list of ScheduledOrder objects.
	 * 
	 * @param orderList
	 * 		List of orders to be scheduled.
	 * @param allTasksCompletedTime
	 * 		The time by which all tasks have to be completed.
	 * @param assemblyLineScheduler
	 * 		The scheduler of the assembly line.
	 * @return A scheduled list of ScheduledOrder objects.
	 */
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(ArrayList<Order> orderList, GregorianCalendar allTasksCompletedTime, AssemblyLineScheduler assemblyLineScheduler);
}

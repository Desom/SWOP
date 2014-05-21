package domain.scheduling.schedulers.algorithm;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.assemblyline.AssemblyLine;
import domain.scheduling.order.Order;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.ScheduledOrder;

public interface AssemblyLineSchedulingAlgorithm {

	/**
	 * Schedules the given list of orders and returns a scheduled list of ScheduledOrder objects.
	 * 
	 * @param orderList
	 * 		List of orders to be scheduled.
	 * @param allTasksCompletedTime
	 * 		The time by which all tasks have to be completed.
	 * @param stateOfAssemblyLine 
	 * @param assemblyLineScheduler
	 * 		The scheduler of the assembly line.
	 * @return A scheduled list of ScheduledOrder objects.
	 */
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(ArrayList<Order> orderList, AssemblyLine assemblyLine);
}

package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.assemblyline.AssemblyLine;
import domain.scheduling.order.Order;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.ScheduledOrder;
import domain.scheduling.schedulers.Scheduler;

public class FIFOSchedulingAlgorithm implements SchedulingAlgorithm {

	private Comparator<Order> comparator;

	/**
	 * Constructor of FIFOSchedulingAlgorithm.
	 */
	public FIFOSchedulingAlgorithm() {
		comparator = new Comparator<Order>(){
			@Override
			public int compare(Order order1, Order order2){
				return order1.getOrderedTime().compareTo(order2.getOrderedTime());
			}
		};
	}
	
	/**
	 * Schedules the given list of orders and returns it.
	 * 
	 * @param orderList
	 * 		List of orders to be scheduled.
	 * @param scheduler
	 * 		The scheduler.
	 * @return A scheduled version of the given list of orders.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList, Scheduler scheduler){
		ArrayList<Order> orderedList = (ArrayList<Order>) orderList.clone();
		Collections.sort(orderedList, this.comparator);
		return orderedList;
	}
	
	public String toString(){
		return "FIFO algorithm";
	}
}

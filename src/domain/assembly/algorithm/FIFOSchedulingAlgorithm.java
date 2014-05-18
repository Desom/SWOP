package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.order.Order;

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
	 * @param assemblyLineScheduler
	 * 		The scheduler of the assembly line.
	 * @return A scheduled version of the given list of orders.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList, AssemblyLineScheduler assemblyLineScheduler) {
		ArrayList<Order> orderedList = (ArrayList<Order>) orderList.clone();
		Collections.sort(orderedList, this.comparator);
		return orderedList;
	}
	
	public String toString(){
		return "FIFO algorithm";
	}
}

package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map.Entry;

import domain.assembly.AssemblyLineScheduler;
import domain.assembly.FactoryScheduler;
import domain.assembly.ScheduledOrder;
import domain.order.Order;

public class BasicFactorySchedulingAlgorithm implements
FactorySchedulingAlgorithm {

	private SchedulingAlgorithm innerAlgorithm;

	/**
	 * Constructor of BasicFactorySchedulingAlgorithm.
	 * 
	 * @param innerAlgorithm
	 * 		The inner algorithm of this basic factory algorithm.
	 */
	public BasicFactorySchedulingAlgorithm(SchedulingAlgorithm innerAlgorithm) {
		this.innerAlgorithm = innerAlgorithm;
	}

	/**
	 * Assigns all the given orders to one of the AssemblyLineSchedulers of factoryScheduler.
	 * 
	 * @param orders
	 * 		The orders which will be assigned to an AssemblyLineScheduler.
	 * @param factoryScheduler
	 * 		The FactoryScheduler who wants to assign these orders to the 
	 * @return
	 * 		A mapping of AssemblyLineSchedulers and their assigned orders.
	 */
	@Override
	public HashMap<AssemblyLineScheduler, ArrayList<Order>> assignOrders(
			ArrayList<Order> orders, FactoryScheduler factoryScheduler) {

		//TODO die null moet hier niet. slecht.
		ArrayList<Order> orderedOrders = this.innerAlgorithm.scheduleToList(orders, null);

		HashMap<AssemblyLineScheduler, ArrayList<ScheduledOrder>> scheduleMapping = new HashMap<AssemblyLineScheduler, ArrayList<ScheduledOrder>>();

		ArrayList<AssemblyLineScheduler> schedulers = factoryScheduler.getSchedulerList();
		for(AssemblyLineScheduler scheduler : schedulers){
			scheduleMapping.put(scheduler, new ArrayList<ScheduledOrder>());
		}
		
		
		
		for(Order order: orderedOrders){
			AssemblyLineScheduler chosenScheduler = null;
			GregorianCalendar timeWithChosen = null;
			ArrayList<ScheduledOrder> newScheduleOfChosen = null;
			for(AssemblyLineScheduler scheduler : schedulers){
				//addToSchedule voegt dan order toe aan het huidige schedule, mss niet de beste methode?
				ArrayList<ScheduledOrder> newSchedule = scheduler.addToSchedule(scheduleMapping.get(scheduler),order);
				GregorianCalendar time = this.findTimeOf(order, newSchedule);
				if(chosenScheduler == null || time.before(timeWithChosen))
					chosenScheduler = scheduler;
					timeWithChosen = time;
					newScheduleOfChosen = newSchedule;
			}
			scheduleMapping.put(chosenScheduler, newScheduleOfChosen);
		}

		return convertToMapping(scheduleMapping);
	}


	/**
	 * Finds the time order will put on the assemblyLine according to schedule.
	 * 
	 * @param order
	 * 		The order whose time is wanted.
	 * @param schedule
	 * 		The schedule which contains a ScheduledOrder with order.
	 * @return The time order will be put on the assemblyLine according to schedule. Null if order not in schedule.
	 */
	private GregorianCalendar findTimeOf(Order order,
			ArrayList<ScheduledOrder> schedule) {
		for(int i = schedule.size() - 1; i >= 0; i--){
			if (schedule.get(i).getScheduledOrder().equals(order)){
				return schedule.get(i).getScheduledTime();
			}
		}
		return null; //TODO goed?
	}

	/**
	 * Convert HashMap<AssemblyLineScheduler, ArrayList<ScheduledOrder>> 
	 * to HashMap<AssemblyLineScheduler, ArrayList<Order>>
	 * by replacing all ScheduledOrders with their Order.
	 * 
	 * @param scheduleMapping
	 * 		The mapping which has to be converted.
	 * @return A HashMap derived from scheduleMapping.
	 */
	private HashMap<AssemblyLineScheduler, ArrayList<Order>> convertToMapping(
			HashMap<AssemblyLineScheduler, ArrayList<ScheduledOrder>> scheduleMapping) {
		
		HashMap<AssemblyLineScheduler, ArrayList<Order>> mapping = new HashMap<AssemblyLineScheduler, ArrayList<Order>>();

		for(Entry<AssemblyLineScheduler, ArrayList<ScheduledOrder>> entry : scheduleMapping.entrySet()){
			ArrayList<Order> newList = new ArrayList<Order>();
			for(ScheduledOrder scheduledOrder : entry.getValue()){
				newList.add(scheduledOrder.getScheduledOrder());
			}
			mapping.put(entry.getKey(), newList);
		}
		
		return mapping;
	}
}

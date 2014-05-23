package domain.scheduling.schedulers.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import domain.InternalFailureException;
import domain.assembly.assemblyline.AssemblyLine;
import domain.configuration.OptionType;
import domain.configuration.VehicleCatalog;
import domain.scheduling.order.Order;
import domain.scheduling.order.SingleTaskOrder;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.FactoryScheduler;
import domain.scheduling.schedulers.ScheduledOrder;

public class BasicSchedulingAlgorithm
	extends AbstractAssemblyLineSchedulingAlgorithm  
	implements FactorySchedulingAlgorithm {

	private SchedulingAlgorithm innerAlgorithm;
	private Comparator<SingleTaskOrder> deadlineComparator = new Comparator<SingleTaskOrder>(){
		@Override
		public int compare(SingleTaskOrder order1, SingleTaskOrder order2){
			return order1.getDeadLine().compareTo(order2.getDeadLine());
		}
	};
	
	/**
	 * Constructor of BasicFactorySchedulingAlgorithm.
	 * 
	 * @param innerAlgorithm
	 * 		The inner algorithm of this basic factory algorithm.
	 */
	protected BasicSchedulingAlgorithm(SchedulingAlgorithm innerAlgorithm) {
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
	 * @throws IllegalStateException
	 * 		If there are no assembly line schedulers.
	 * @throws IllegalArgumentException
	 * 		If there are no assembly lines which can complete an order in the given list of orders.
	 */
	@Override
	public HashMap<AssemblyLineScheduler, ArrayList<Order>> assignOrders(
			ArrayList<Order> orders, FactoryScheduler factoryScheduler) throws IllegalStateException, IllegalArgumentException {

		ArrayList<Order> sortedOrders = this.innerAlgorithm.scheduleToList(orders, factoryScheduler);

		HashMap<AssemblyLineScheduler, ArrayList<Order>> scheduleMapping = new HashMap<AssemblyLineScheduler, ArrayList<Order>>();

		ArrayList<AssemblyLineScheduler> schedulers = factoryScheduler.getSchedulerList();
		for(AssemblyLineScheduler scheduler : schedulers){
			scheduleMapping.put(scheduler, new ArrayList<Order>());
		}
		
		ArrayList<SingleTaskOrder> singleTasks = combSingleTaskOrders(sortedOrders);
		ArrayList<SingleTaskOrder> beginST = combSingleTaskOrdersByType(singleTasks, VehicleCatalog.taskTypeCreator.Color);
		ArrayList<SingleTaskOrder> endST = combSingleTaskOrdersByType(singleTasks, VehicleCatalog.taskTypeCreator.Seats);
		Collections.sort(beginST, deadlineComparator);
		Collections.sort(endST, deadlineComparator);
		
		for(Order order: sortedOrders){
			AssemblyLineScheduler chosenScheduler = null;
			GregorianCalendar timeWithChosen = null;
			ArrayList<ScheduledOrder> newScheduleOfChosen = null;
			for(AssemblyLineScheduler scheduler : schedulers){
				if(!scheduler.canScheduleOrder(order)){
					continue;
				}
				//addToSchedule voegt dan order toe aan het huidige schedule, mss niet de beste methode?
				ArrayList<Order> newList = new ArrayList<Order>(scheduleMapping.get(scheduler));
				newList.add(order);
				ArrayList<ScheduledOrder> newSchedule = scheduler.getCurrentAlgorithm().scheduleToScheduledOrderList(newList, scheduler.getAssemblyLine());
				GregorianCalendar time = this.findScheduledOrderOf(order, newSchedule).getCompletedTime();
				if(chosenScheduler == null || time.before(timeWithChosen)){
					chosenScheduler = scheduler;
					timeWithChosen = time;
					newScheduleOfChosen = newSchedule;
				}
			}
			if(chosenScheduler != null){
				scheduleMapping.get(chosenScheduler).add(order);
			}
			else{
				if(schedulers.isEmpty()){
					throw new IllegalStateException("BasicScedulingAlgorithm can't order if there are no AssemblyLineSchedulers.");
				}
				throw new IllegalArgumentException("BasicSchedulingAlgorithm can't order an order which can't be done by any AssembyLineScheduler.");
			}
		}

		scheduleSingleTasks(scheduleMapping, schedulers, beginST);
		scheduleSingleTasks(scheduleMapping, schedulers, endST);
		

		return scheduleMapping;
	}

	/**
	 * Schedules the single task orders.
	 * 
	 * @param scheduleMapping
	 * @param schedulers
	 * @param stoList
	 */
	private void scheduleSingleTasks(
			HashMap<AssemblyLineScheduler, ArrayList<Order>> scheduleMapping,
			ArrayList<AssemblyLineScheduler> schedulers,
			ArrayList<SingleTaskOrder> stoList) {
		
		ArrayList<SingleTaskOrder> deadlineFailure = new ArrayList<SingleTaskOrder>();
		
		int count = 0;
		SingleTaskOrder sto = null;
		while(!stoList.isEmpty() || count >= schedulers.size()){
			if(sto == null){
				sto = stoList.remove(0); 
			}
			for(AssemblyLineScheduler scheduler : schedulers){
				
				if(!canDoSingleTask(sto, scheduler, scheduleMapping)){
					count++;
					if(count >= schedulers.size()){
						deadlineFailure.add(sto);
						count = 0;
						if(!stoList.isEmpty()){
							sto = stoList.remove(0);
						}
						else{
							break;
						}
					}
				}
				else{
					scheduleMapping.get(scheduler).add(sto);
					count = 0;
					if(!stoList.isEmpty()){
						sto = stoList.remove(0);
					}
					else{
						break;
					}
				}
			}
		}
		
		for(int i = 0; i < deadlineFailure.size(); i++){
			int a = i % schedulers.size();
			scheduleMapping.get(schedulers.get(a)).add(deadlineFailure.get(i));
		}
	}

	
	private boolean canDoSingleTask(SingleTaskOrder sto,
			AssemblyLineScheduler scheduler,
			HashMap<AssemblyLineScheduler, ArrayList<Order>> scheduleMapping) {
		ArrayList<Order> newList = new ArrayList<Order>(scheduleMapping.get(scheduler));
		newList.add(sto);
		ArrayList<ScheduledOrder> newSchedule = scheduler.getCurrentAlgorithm().scheduleToScheduledOrderList(newList, scheduler.getAssemblyLine());
		GregorianCalendar time = this.findScheduledOrderOf(sto, newSchedule).getCompletedTime();

		return !time.after(sto.getDeadLine());
	}

	/**
	 * Finds the time order will put on the assemblyLine according to schedule.
	 * 
	 * @param order
	 * 		The order whose time is wanted.
	 * @param schedule
	 * 		The schedule which contains a ScheduledOrder with order.
	 * @return The time order will be put on the assemblyLine according to schedule.
	 * @throws IllegalArgumentException
	 * 		If the given order cannot be found in the given schedule list. 
	 */
	private ScheduledOrder findScheduledOrderOf(Order order, ArrayList<ScheduledOrder> schedule) {
		for(int i = schedule.size() - 1; i >= 0; i--){
			if (order.equals(schedule.get(i).getScheduledOrder())){
				return schedule.get(i);
			}
		}
		throw new IllegalArgumentException("Can't find the given order in the given list.");
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
	
	/**
	 * Schedules the given list of orders and returns a scheduled list of ScheduledOrder objects.
	 * 
	 * @param orderList
	 * 		List of orders to be scheduled.
	 * @param allTasksCompletedTime
	 * 		The time by which all tasks have to be completed.
	 * @param assemblyLineScheduler
	 * @return A scheduled list of ScheduledOrder objects.
	 */
	@Override
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(
			ArrayList<Order> orderList, 
			AssemblyLine assemblyLine) {
		throw new InternalFailureException("not implemented");
	}
	
	/**
	 * Filters all single task orders out of the given list of orders and returns these single tasks orders.
	 * 
	 * @param orders
	 *		The list where SingleTaskOrders will be extracted from.
	 * @return A list with all the SingleTaskOrders out of orderList.
	 * 		OrderList does not contain any SingleTaskOrders anymore.
	 */
	private ArrayList<SingleTaskOrder> combSingleTaskOrders(
			ArrayList<Order> orders) {
		ArrayList<SingleTaskOrder> result = new ArrayList<SingleTaskOrder>();
		for(int i = 0; i< orders.size(); i++){
			if(orders.get(i) instanceof SingleTaskOrder){
				result.add((SingleTaskOrder) orders.remove(i));
				i--;
			}
		}
		return result;
	}

	/**
	 * Returns all SingleTaskOrders with a specific type in the given list.
	 * @param singleTaskOrders
	 *		The list where SingleTaskOrders with a specific type will be retrieved from.
	 * @param type
	 * 		The type of the SingleTaskOrders that will be extracted.
	 * @return A list with all the SingleTaskOrders out of orderList.
	 */
	private ArrayList<SingleTaskOrder> combSingleTaskOrdersByType(
			ArrayList<SingleTaskOrder> singleTaskOrders, OptionType type) {
		ArrayList<SingleTaskOrder> result = new ArrayList<SingleTaskOrder>();
		for(SingleTaskOrder i:singleTaskOrders){
			if(i.getType() == type){
				result.add(i);
			}
		}
		return result;
	}
	
	/**
	 * Returns the inner algorithm of this efficiency scheduling algorithm.
	 * 
	 * @return The inner algorithm of this efficiency scheduling algorithm.
	 */
	public SchedulingAlgorithm getInnerAlgorithm() {
		return innerAlgorithm;
	}
	
	public String toString(){
		return "Basic scheduling algorithm using " + this.innerAlgorithm.toString();
	}
}

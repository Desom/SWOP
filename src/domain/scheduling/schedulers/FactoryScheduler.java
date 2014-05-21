package domain.scheduling.schedulers;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;





import domain.assembly.algorithm.FactorySchedulingAlgorithm;
import domain.scheduling.order.Order;
import domain.scheduling.order.SingleTaskOrder;

public class FactoryScheduler implements Scheduler,OrderHandler {

	private ArrayList<AssemblyLineScheduler> schedulerList;
	private FactorySchedulingAlgorithm currentAlgorithm;
	private boolean outDated;
	private HashMap<AssemblyLineScheduler,ArrayList<Order>> ordersForSchedulers;
	private OrderHandler orderHandler;
	private ArrayList<FactorySchedulingAlgorithm> possibleAlgorithms;
	
	/**
	 * Constructor of FactoryScheduler.
	 * 
	 * @param schedulerList
	 * 		The assembly line schedulers which this order will control.
	 * @param possibleAlgorithms
	 * 		Possible algorithms to schedule orders.
	 * 		The first algorithm of this list is the default algorithm.
	 */
	public FactoryScheduler(ArrayList<AssemblyLineScheduler> schedulerList, ArrayList<FactorySchedulingAlgorithm> possibleAlgorithms){
		this.schedulerList = new ArrayList<AssemblyLineScheduler>(schedulerList);
		this.possibleAlgorithms = new ArrayList<FactorySchedulingAlgorithm>(possibleAlgorithms);
		this.currentAlgorithm = this.possibleAlgorithms.get(0);
		outDated = true;

		for(AssemblyLineScheduler scheduler : this.schedulerList){
			scheduler.setOrderHandler(this);
		}
		
	}
	
	/**
	 * Returns the current time of the system. The oldest time out of all the current times of the AssemblyLineSchedulers.
	 * 
	 * @return The current time of the system.
	 */
	@Override
	public GregorianCalendar getCurrentTime() {
		GregorianCalendar time = null;
		for(AssemblyLineScheduler als: schedulerList){
			if(time == null || als.getCurrentTime().before(time)){
				time = als.getCurrentTime();
			}
		}
		
		return time;
	}

	/**
	 * Returns the estimated time on which the given order will be completed.
	 * 
	 * @param order
	 * 		The order for which the completion estimate has to be returned.
	 * @return The estimated time on which the given order will be completed.
	 */
	@Override
	public GregorianCalendar completionEstimate(Order order) {
		AssemblyLineScheduler scheduler = this.findScheduler(order);
		return scheduler.completionEstimate(order);
	}

	/**
	 * Find the AssemblyLineScheduler that has to schedule the given Order.
	 * 
	 * @param order
	 * 		The Order whose AssemblyLineScheduler we want to find.
	 * @return The AssemblyLineScheduler that has to schedule order.
	 */
	private AssemblyLineScheduler findScheduler(Order order) {
		HashMap<AssemblyLineScheduler, ArrayList<Order>> ordersForSchedulers = this.getOrdersForSchedulers();
		for(AssemblyLineScheduler scheduler : this.schedulerList){
			if(ordersForSchedulers.get(scheduler).contains(order)){
				return scheduler;
			}
		}
		throw new IllegalArgumentException("The FactoryScheduler:" + this + " doesn't schedule the given Order:" + order);

	}

	/**
	 * Returns the mapping with the assignment of orders for each AssemblyLineScheduler.
	 * 
	 * @return The mapping of orders to AssemblyLineSchedulers.
	 */
	private HashMap<AssemblyLineScheduler,ArrayList<Order>> getOrdersForSchedulers() {
		//controleer of de orders die nog moeten worden gedaan, nog steeds moeten worden gedaan.
		//Mss vergelijken met AssemblyLineScheduler ipv OrderHandler?
		ArrayList<Order> orders = this.getOrdersToBeScheduled();
		for(ArrayList<Order> orderList : this.ordersForSchedulers.values()){
			if(!orders.containsAll(orderList)){
				this.updateSchedule();
				break;
			}
		}
		
		//als outDated schedule, maak nieuw.
		if(outDated){
			this.ordersForSchedulers = this.currentAlgorithm.assignOrders(orders,this);
			outDated = false;
		}
		
		return this.ordersForSchedulers;
	}

	/**
	 * Set the order handler.
	 * 
	 * @param orderHandler
	 * 		The orderHandler to be set.
	 * @throws IllegalStateException
	 * 		If this factory scheduler is already coupled with an order handler.
	 * @throws IllegalArgumentException
	 * 		If the given order handler is already coupled to another assembly line scheduler.
	 */
	public void setOrderHandler(OrderHandler orderHandler) throws IllegalStateException, IllegalArgumentException {
		if(this.orderHandler != null){
			if(this.orderHandler.hasScheduler(this)){
				throw new IllegalStateException("The FactoryScheduler: " + this.toString() + " is already coupled with an OrderHandler");
			}
		}
		if(!orderHandler.hasScheduler(this)){
			throw new IllegalArgumentException("The given OrderHandler isn't coupled with this FactoryScheduler.");
		}
		this.orderHandler = orderHandler;
	}

	/**
	 * Signals the FactoryScheduler that the schedule and AssemlyLineSchedulers need to be updated.
	 */
	@Override
	public void updateSchedule() {
		this.outDated = true;
		for(AssemblyLineScheduler scheduler : this.schedulerList){
			scheduler.updateSchedule();
		}
	}
	
	//TODO hoe doen we dit best? (huidige code mag  veranderd worden.)
	@Override
	public boolean canFinishOrderBeforeDeadline(
			SingleTaskOrder orderWithDeadline) {
		ArrayList<Order> orders = this.orderHandler.getOrdersFor(this);
		orders.add(orderWithDeadline);
		@SuppressWarnings("unused")
		HashMap<AssemblyLineScheduler, ArrayList<Order>> assigned = this.currentAlgorithm.assignOrders(orders, this);
		//TODO
		return false;
	}

	/**
	 * Checks if this Scheduler has the means to complete the given order.
	 * 
	 * @param order
	 * 		The order for which will be checked.
	 * @return True if the order can be completed, false otherwise.
	 */
	@Override
	public boolean canScheduleOrder(Order order) {
		for(AssemblyLineScheduler als : this.schedulerList){
			if(als.canScheduleOrder(order)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the orders it wants the given scheduler to schedule.
	 * 
	 * @param scheduler
	 * 		The Scheduler that will schedule the returned Orders.
	 * @return The Orders which have to be scheduled by the given scheduler.
	 */
	//TODO wat als scheduler niet hoort bij deze orderhandler? null,exception,lege lijst??? Zie OrderManager,FactoryScheduler
	@Override
	public ArrayList<Order> getOrdersFor(Scheduler scheduler) {
		for(AssemblyLineScheduler als : this.schedulerList){
			if(als.equals(scheduler)){
				return this.getOrdersForSchedulers().get(als);
			}
		}
		
		return null; //TODO goed? IllegalArgumentException
	}

	
	@Override
	public boolean hasScheduler(Scheduler scheduler) {
		return this.schedulerList.contains(scheduler);
	}

	/**
	 * Returns the list of AssemblyLineSchedulers controlled by this FactoryScheduler.
	 * 
	 * @return The list of AssemblyLineSchedulers.
	 */
	public ArrayList<AssemblyLineScheduler> getSchedulerList() {
		return new ArrayList<AssemblyLineScheduler>(schedulerList);
	}

	/**
	 * Returns the current algorithm of this factory scheduler.
	 * 
	 * @return The current algorithm of this factory scheduler.
	 */
	public FactorySchedulingAlgorithm getCurrentAlgorithm() {
		return this.currentAlgorithm;
	}

	/**
	 * Returns all possible algorithms of this factory scheduler.
	 * 
	 * @return All possible algorithms of this factory scheduler.
	 */
	public ArrayList<FactorySchedulingAlgorithm> getPossibleAlgorithms() {
		return new ArrayList<FactorySchedulingAlgorithm>(this.possibleAlgorithms);
	}

	/**
	 * Returns the orders to be scheduled.
	 * 
	 * @return The orders to be scheduled, empty if this factory scheduler has no order handler.
	 */
	@Override
	public ArrayList<Order> getOrdersToBeScheduled() {
		if(this.orderHandler != null){
			ArrayList<Order> orders = this.orderHandler.getOrdersFor(this);
			for(AssemblyLineScheduler assemblyLineScheduler : this.getSchedulerList()){
				//ok of moet dit aan alscheduer worden gevraagd en die vraagt dat dan aan assemblyLine? Ja, chainen
				LinkedList<Order> onAssembly = assemblyLineScheduler.getAssemblyLine().getAllOrders();
				orders.removeAll(onAssembly);
			}
			return orders;
		}
		return new ArrayList<Order>();
	}

	/**
	 * Sets the scheduling algorithm.
	 * 
	 * @param algorithm
	 * 		The new active scheduling algorithm.
	 * @throws IllegalArgumentException
	 * 		If the given algorithm isn't one of the possible algorithms.
	 */
	public void setSchedulingAlgorithm(FactorySchedulingAlgorithm algorithm) throws IllegalArgumentException {
		if(!this.possibleAlgorithms.contains(algorithm))
			throw new IllegalArgumentException("This FactorySchedulingAlgorithm is not one of the possible FactorySchedulingAlgorithm");
		this.currentAlgorithm = algorithm;
		this.updateSchedule();
	}

	/**
	 * Sets the current algorithm to the default algorithm.
	 * Updates the schedule afterwards.
	 */
	public void setSchedulingAlgorithmToDefault(){
		this.setSchedulingAlgorithm(this.getPossibleAlgorithms().get(0));
		this.updateSchedule();
	}

}
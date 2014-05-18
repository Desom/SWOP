package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import domain.assembly.algorithm.FactorySchedulingAlgorithm;
import domain.order.Order;
import domain.order.SingleTaskOrder;

public class FactoryScheduler implements Scheduler,OrderHandler {

	private ArrayList<AssemblyLineScheduler> schedulerList;
	private FactorySchedulingAlgorithm currentAlgorithm;
	private boolean outDated;
	private HashMap<AssemblyLineScheduler,ArrayList<Order>> ordersForSchedulers;
	private OrderHandler orderHandler;
	
	/**
	 * Returns the current time of the system. The oldest time out of all the current times of the AssemblyLineSchedulers.
	 * 
	 * @return The current time of the system.
	 */
	@Override
	public GregorianCalendar getCurrentTime() {
		//TODO goede currentTime of moet die anders?
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
		ArrayList<Order> orders = this.orderHandler.getOrdersFor(this);
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
		
		return null; //TODO goed?
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

}

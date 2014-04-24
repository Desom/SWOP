package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.InternalFailureException;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.order.Order;
import domain.order.OrderManager;
import domain.order.SingleTaskOrder;

public class AssemblyLineScheduler implements Scheduler{

	public static final int END_OF_DAY = 22;
	public static final int BEGIN_OF_DAY = 6;
	private int overTimeInMinutes;
	private AssemblyLine assemblyLine;
	private ArrayList<SchedulingAlgorithm> possibleAlgorithms;
	private SchedulingAlgorithm currentAlgorithm;
	private GregorianCalendar currentTime;
	private OrderManager orderManager;
	private ArrayList<ScheduledOrder> schedule;
	private boolean outDated;

	/**
	 * Constructor of AssemblyLineScheduler.
	 * 
	 * @param time
	 * 		The current time.
	 * @param possibleAlgorithms
	 * 		Possible algorithms to schedule orders.
	 * 		The first algorithm of this list is the default algorithm.
	 */
	@SuppressWarnings("unchecked")
	public AssemblyLineScheduler(GregorianCalendar time, ArrayList<SchedulingAlgorithm> possibleAlgorithms) {
		this.currentTime = (GregorianCalendar) time.clone();
		this.possibleAlgorithms = (ArrayList<SchedulingAlgorithm>) possibleAlgorithms.clone();
		this.currentAlgorithm = this.possibleAlgorithms.get(0);
		this.outDated = true;
	}
	
	//TODO docs
	private GregorianCalendar calculateEstimatedCompletionTimeOf(Order order, ArrayList<ScheduledOrder> scheduledOrders, GregorianCalendar futureTime){
		//TODO refactoring?
		if(order == null){
			throw new IllegalArgumentException("It is impossible to calculate the completionEstimate with null.");
		}
		
		if(this.getAssemblyLine() == null){
			throw new InternalFailureException("An AssemblyLineScheduler doesn't have an AssemblyLine which is necessary for completionEstimate.");
		}
		
		//als order al op de AssemblyLine staat
		if(this.getAssemblyLine().getAllOrders().contains(order)){
			
			LinkedList<Order> assembly = this.getAssemblyLine().getAllOrders();
			int i = 0;
			while(assembly.contains(order)){
				if(order.equals(assembly.getLast())){
					return futureTime;
				}
				assembly.removeLast();
				if(scheduledOrders != null && scheduledOrders.size() > i){
					assembly.addFirst(scheduledOrders.get(i).getScheduledOrder());
					i++;
				}
				else{
					assembly.addFirst(null);
				}
				int timeTillAdvance = this.getAssemblyLine().calculateTimeTillAdvanceFor(assembly);
				futureTime.add(GregorianCalendar.MINUTE, timeTillAdvance);
			}
		}
		
		if(scheduledOrders == null){
			throw new IllegalArgumentException("The AssemblyLineScheduler:" + this + " doesn't schedule the given Order:" + order);
		}
		
		int position = 0;
		for(; position < scheduledOrders.size(); position++){
			if(order.equals(scheduledOrders.get(position).getScheduledOrder())){
				break;
			}
		}
		
		if(position >= scheduledOrders.size()){
			throw new IllegalArgumentException("The AssemblyLineScheduler:" + this + " doesn't schedule the given Order:" + order);
		}
		return timeTillOrderOffAssemblyLine(position, scheduledOrders);

	}


	/**
	 * TODO
	 * @param position
	 * @param scheduledOrders
	 * @return
	 */
	private GregorianCalendar timeTillOrderOffAssemblyLine(int position, ArrayList<ScheduledOrder> scheduledOrders) {
		GregorianCalendar simulTime = scheduledOrders.get(position).getScheduledTime();
		LinkedList<Order> assembly;
		if(position < 2){
			assembly = this.getAssemblyLine().getAllOrders();
			assembly.removeLast();
			assembly.addFirst(scheduledOrders.get(0).getScheduledOrder());

			if(position == 1){
				assembly.removeLast();
				assembly.addFirst(scheduledOrders.get(1).getScheduledOrder());
			}

		}
		else{
			assembly = new LinkedList<Order>();
			assembly.addFirst(scheduledOrders.get(position-2).getScheduledOrder());
			assembly.addFirst(scheduledOrders.get(position-1).getScheduledOrder());
			assembly.addFirst(scheduledOrders.get(position).getScheduledOrder());
		}
		for(int i = position+1; i < position + 4; i ++){
			simulTime.add(GregorianCalendar.MINUTE, this.getAssemblyLine().calculateTimeTillAdvanceFor(assembly));
			assembly.removeLast();
			if(scheduledOrders.size() > i){
				assembly.addFirst(scheduledOrders.get(i).getScheduledOrder());
			}
			else{
				assembly.addFirst(null);
			}
		}

		return simulTime;
	}
	
	/**
	 * Returns the estimated time of completion of this given order.
	 * 
	 * @param order
	 * 		The order of which the completion estimate is desired.
	 * @return The estimated date of completion of the given order.
	 */
	public GregorianCalendar completionEstimate(Order order){
		int time = this.getAssemblyLine().calculateTimeTillAdvanceFor(this.getAssemblyLine().getAllOrders());
		GregorianCalendar futureTime = this.getCurrentTime();
		futureTime.add(GregorianCalendar.MINUTE, time);
		
		ArrayList<ScheduledOrder> scheduledOrders = null;
		try {
			scheduledOrders = this.getSchedule(futureTime);
		} catch (NoOrdersToBeScheduledException e) {
			if(!this.assemblyLine.getAllOrders().contains(order))
				throw new IllegalArgumentException("The AssemblyLineScheduler:" + this + " doesn't schedule the given Order:" + order);
		}
		return calculateEstimatedCompletionTimeOf(order, scheduledOrders, futureTime);
	}
	
	/**
	 * Checks wither the given order can be finished before its deadline.
	 * 
	 * @param orderWithDeadline
	 * 		The order which has to be checked.
	 * @return True if the order can be finished before the deadline, otherwise false.
	 */
	public boolean canFinishOrderBeforeDeadline(SingleTaskOrder orderWithDeadline){

		int time = this.getAssemblyLine().calculateTimeTillAdvanceFor(this.getAssemblyLine().getAllOrders());
		GregorianCalendar futureTime = this.getCurrentTime();
		futureTime.add(GregorianCalendar.MINUTE, time);
		
		int amountOfDeadlineFailures = 0;
		
		ArrayList<ScheduledOrder> scheduledOrders = null;
		try {
			scheduledOrders = this.getSchedule((GregorianCalendar) futureTime.clone());
			amountOfDeadlineFailures = this.calculateAmountOfDeadlineFailures(scheduledOrders);
		} catch (NoOrdersToBeScheduledException e) {}
		
		ArrayList<Order> orders = this.getOrdersToBeScheduled();
		orders.add(orderWithDeadline);
		ArrayList<ScheduledOrder> scheduledOrdersWithExtra = this.currentAlgorithm.scheduleToScheduledOrderList(orders, (GregorianCalendar) futureTime.clone(), this);
		int newAmountOfDeadlineFailures = this.calculateAmountOfDeadlineFailures(scheduledOrdersWithExtra);
		
		return amountOfDeadlineFailures >= newAmountOfDeadlineFailures;
	}
	
	/**
	 * Returns the number of orders which won't be completed before their deadline.
	 * 
	 * @param scheduledOrders
	 * 		The list of scheduled orders to check.
	 * @return The number of orders which won't be completed before their deadline.
	 */
	private int calculateAmountOfDeadlineFailures(ArrayList<ScheduledOrder> scheduledOrders) {
		int amount = 0;
		for(int i = 0; i < scheduledOrders.size(); i++){
			Order order = scheduledOrders.get(i).getScheduledOrder();
			if (order instanceof SingleTaskOrder){
				SingleTaskOrder singleTask = (SingleTaskOrder) order;
				if(this.timeTillOrderOffAssemblyLine(i, scheduledOrders).after(singleTask.getDeadLine())){
					amount++;
				}
			}
		}
		return amount;
	}

	/**
	 * Returns the Order that will be put on the AssemblyLine immediately after updating
	 * the currentTime with the given amount of minutes.
	 * 
	 * @param minutes
	 * 		The amount of minutes it took to complete the tasks in the Workstations. (in minutes)
	 * @return The Order that is scheduled to be built now.
	 * @throws NoOrdersToBeScheduledException
	 * 		If there are no orders to be scheduled.
	 */
	protected Order getNextOrder(int minutes) throws NoOrdersToBeScheduledException{
		this.addCurrentTime(minutes);
		GregorianCalendar now = this.getCurrentTime();
		ArrayList<ScheduledOrder> scheduledOrders = getSchedule(now);
		int i = 0;
		while(this.getAssemblyLine() != null 
				&& this.getAssemblyLine().isEmpty() 
				&& scheduledOrders.get(i).getScheduledOrder() == null){
			i++;
			if(i >= scheduledOrders.size()){
				throw new NoOrdersToBeScheduledException();
			}
		}
		if(scheduledOrders.get(i).getScheduledTime().equals(this.getCurrentTime())){
			return scheduledOrders.get(i).getScheduledOrder();
		}
		if(this.getAssemblyLine() != null && this.getAssemblyLine().isEmpty() && scheduledOrders.get(i).getScheduledTime().get(GregorianCalendar.HOUR_OF_DAY) == AssemblyLineScheduler.BEGIN_OF_DAY){
			this.startNewDay();
			return scheduledOrders.get(i).getScheduledOrder();
		}
		
		throw new InternalFailureException("The currentAlgorithm didn't schedule an order for now even though he should have.");
		
	}

	/**
	 * Returns the Order that will be put on the assembly after the given amount
	 * of minutes, but without updating currentTime.
	 * 
	 * @param minutes
	 * 		The amount of minutes it took to complete the tasks in the
	 *      Workstations. (in minutes)
	 * @return The CarOrder that is scheduled to be built after the given amount of minutes.
	 * @throws NoOrdersToBeScheduledException
	 * 		If there are no orders to be scheduled.
	 */
	protected Order seeNextOrder(int minutes) throws NoOrdersToBeScheduledException{
		GregorianCalendar futureTime = this.getCurrentTime();
		futureTime.add(GregorianCalendar.MINUTE, minutes);
		ArrayList<ScheduledOrder> scheduledOrders = getSchedule(futureTime);
		int i = 0;
//		while(this.getAssemblyLine() != null 
//				&& this.getAssemblyLine().isEmpty() 
//				&& scheduledOrders.get(i).getScheduledOrder() == null){
//			i++;
//			if(i >= scheduledOrders.size()){
//				throw new NoOrdersToBeScheduledException();
//			}
//		} TODO fout in while, deze while hoefde eigenlijk niet denk ik.
		if(scheduledOrders.get(i).getScheduledTime().equals(futureTime)){
			return scheduledOrders.get(i).getScheduledOrder();
		}
		if(this.getAssemblyLine().isEmpty() && scheduledOrders.get(i).getScheduledTime().get(GregorianCalendar.HOUR_OF_DAY) == AssemblyLineScheduler.BEGIN_OF_DAY){
			return scheduledOrders.get(i).getScheduledOrder();
		}
		
		throw new InternalFailureException("The currentAlgorithm didn't schedule an order for now even though he should have.");
		
	}

	/**
	 * Returns the schedule (a list of ScheduledOrder objects) of this assembly line scheduler.
	 * 
	 * @param futureTime
	 * 		
	 * @return The schedule of this assembly line scheduler.
	 * @throws NoOrdersToBeScheduledException
	 * 		If the schedule of this assembly line scheduler is empty.
	 */
	//TODO controleer ifs
	private ArrayList<ScheduledOrder> getSchedule(GregorianCalendar futureTime) throws NoOrdersToBeScheduledException {
		if(this.outDated 
				|| this.schedule == null 
				|| this.schedule.size() == 0
				|| !futureTime.equals(this.schedule.get(0).getScheduledTime())){
			this.schedule = this.getCurrentAlgorithm().scheduleToScheduledOrderList(this.getOrdersToBeScheduled(), futureTime, this);
			this.outDated = false;
		}
		if(this.getAssemblyLine() != null && !this.schedule.isEmpty() && this.getAssemblyLine().getAllOrders().contains(this.schedule.get(0).getScheduledOrder())){
			this.schedule = this.getCurrentAlgorithm().scheduleToScheduledOrderList(this.getOrdersToBeScheduled(), futureTime, this);
		}
		
		if(this.schedule.isEmpty()){
			throw new NoOrdersToBeScheduledException();
		}
		return this.schedule;
	}
	
	/**
	 * This method is called to indicate that the schedule of this assembly line scheduler is outdated. 
	 */
	@Override
	public void updateSchedule(){
		this.outDated = true;
		if(this.getAssemblyLine() != null && this.getAssemblyLine().isEmpty()){
			try{
				this.getAssemblyLine().advanceLine();
			}
			catch(CannotAdvanceException e){
				throw new InternalFailureException("The AssemblyLine couldn't advance even though it was empty.");
			}
		}
	}

	/**
	 * Returns the orders to be scheduled.
	 * Returns null if this assembly line scheduler has no order manager.
	 * 
	 * @return The orders to be scheduled, or null if this assembly line scheduler has no order manager.
	 */
	public ArrayList<Order> getOrdersToBeScheduled() {
		if(this.getOrderManager() != null){
			ArrayList<Order> orders = this.getOrderManager().getAllUnfinishedOrders();
			LinkedList<Order> onAssembly = this.getAssemblyLine().getAllOrders();
			orders.removeAll(onAssembly);
			return orders;
		}
		return null;
	}

	/**
	 * Sets the scheduling algorithm.
	 * 
	 * @param algorithm
	 * 		The new active scheduling algorithm.
	 * @throws IllegalArgumentException
	 * 		If the given algorithm isn't one of the possible algorithms.
	 */
	public void setSchedulingAlgorithm(SchedulingAlgorithm algorithm) throws IllegalArgumentException {
		if(!this.possibleAlgorithms.contains(algorithm))
			throw new IllegalArgumentException("This SchedulingAlgorithm is not one of the possible SchedulingAlgorithms");
		this.currentAlgorithm = algorithm;
		this.updateSchedule();
	}
	
	/**
	 * Sets the current algorithm to the default algorithm.
	 * Updates the schedule afterwards.
	 */
	public void setSchedulingAlgorithmToDefault() {
		this.currentAlgorithm = this.getPossibleAlgorithms().get(0);
		this.updateSchedule();
	}

	/**
	 * Returns all possible algorithms of this assembly line scheduler.
	 * 
	 * @return All possible algorithms of this assembly line scheduler.
	 */
	public ArrayList<SchedulingAlgorithm> getPossibleAlgorithms() {
		return possibleAlgorithms;
	}
	
	/**
	 * Returns the current algorithm of this assembly line scheduler.
	 * 
	 * @return The current algorithm of this assembly line scheduler.
	 */
	public SchedulingAlgorithm getCurrentAlgorithm(){
		return this.currentAlgorithm;
	}

	/**
	 * Returns the current time of this assembly line scheduler.
	 * 
	 * @return The current time of this assembly line scheduler.
	 */
	public GregorianCalendar getCurrentTime() {
		return (GregorianCalendar) this.currentTime.clone();
	}
	
	/**
	 * Adds minutes to the current time of this assembly line scheduler.
	 * 
	 * @param minutes
	 * 		The minutes to be added to the current time.
	 */
	private void addCurrentTime(int minutes){
		this.currentTime.add(GregorianCalendar.MINUTE, minutes);
	}

	/**
	 * Starts a new day by updating the overtime and setting the current time to the start of the next work day.
	 */
	private void startNewDay() {
		this.updateOverTime();
		GregorianCalendar newDay = new GregorianCalendar(
				this.currentTime.get(GregorianCalendar.YEAR),
				this.currentTime.get(GregorianCalendar.MONTH),
				this.currentTime.get(GregorianCalendar.DAY_OF_MONTH),
				AssemblyLineScheduler.BEGIN_OF_DAY,
				0,
				0);
		if(newDay.before(this.currentTime))
			newDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
		this.currentTime = newDay;
	}
	
	/**
	 * Updates the overtime, based on the current time and the real end of day time.
	 * Will only be called when it's the end of the work day.
	 */
	private void updateOverTime() {
		GregorianCalendar time = (GregorianCalendar) this.getCurrentTime().clone();
		GregorianCalendar endOfDay = this.getRealEndOfDay();
		if(time.before(endOfDay)){
			this.overTimeInMinutes = 0;
			return;
		}
		
		//TODO kleine verbetering, ok?
		int newOverTime = 0;
		while(!time.equals(endOfDay)){
			time.add(GregorianCalendar.MINUTE, -1);
			newOverTime++;
		}
		
		this.overTimeInMinutes = newOverTime;
	}

	/**
	 * Returns the end of the day with overtime taken into account.
	 * 
	 * @return The end of the day with overtime taken into account.
	 */
	public GregorianCalendar getRealEndOfDay() {
		GregorianCalendar current = this.getCurrentTime();
		GregorianCalendar endOfDay = new GregorianCalendar(
				current.get(GregorianCalendar.YEAR),
				current.get(GregorianCalendar.MONTH),
				current.get(GregorianCalendar.DAY_OF_MONTH),
				AssemblyLineScheduler.END_OF_DAY,
				0,
				0);
		if(current.get(GregorianCalendar.HOUR_OF_DAY) < AssemblyLineScheduler.BEGIN_OF_DAY){
			endOfDay.add(GregorianCalendar.DAY_OF_MONTH, -1);
		}
		endOfDay.add(GregorianCalendar.MINUTE, - this.overTimeInMinutes);
		return endOfDay;
	}

	/**
	 * Sets the assembly line.
	 * 
	 * @param assemblyLine
	 * 		The assembly line to be set.
	 * @throws IllegalStateException
	 * 		If this assembly line scheduler is already coupled with another another assembly line.
	 * @throws IllegalArgumentException
	 * 		If the given assembly line is already coupled with another assembly line scheduler.
	 */
	public void setAssemblyLine(AssemblyLine assemblyLine) throws IllegalStateException, IllegalArgumentException {
		if(this.assemblyLine != null){
			if(this.assemblyLine.getAssemblyLineScheduler() == this){
				throw new IllegalStateException("The AssemblyLineScheduler: " + this.toString() + " is already coupled with an AssemblyLine");
			}
		}
		if(assemblyLine.getAssemblyLineScheduler() != this){
			throw new IllegalArgumentException("The given AssemblyLine is already coupled with a different Scheduler.");
		}
		this.assemblyLine = assemblyLine;
	}
	
	/**
	 * Returns the assembly line.
	 * 
	 * @return The assembly line.
	 */
	public AssemblyLine getAssemblyLine() {
		return assemblyLine;
	}

	/**
	 * Set the order manager.
	 * 
	 * @param orderManager
	 * 		The order manager to be set.
	 * @throws IllegalStateException
	 * 		If this assembly line scheduler is already coupled with an order manager.
	 * @throws IllegalArgumentException
	 * 		If the given order manager is already coupled to another assembly line scheduler.
	 */
	public void setOrderManager(OrderManager orderManager) throws IllegalStateException, IllegalArgumentException {
		if(this.orderManager != null){
			if(this.orderManager.getScheduler() == this){
				throw new IllegalStateException("The AssemblyLineScheduler: " + this.toString() + " is already coupled with an OrderManager");
			}
		}
		if(orderManager.getScheduler() != this){
			throw new IllegalArgumentException("The given OrderManager is already coupled with a different Scheduler.");
		}
		this.orderManager = orderManager;
	}
	
	/**
	 * Returns the order manager of this assembly line scheduler.
	 * 
	 * @return The order manager of this assembly line scheduler.
	 */
	private OrderManager getOrderManager() {
		return orderManager;
	}
}

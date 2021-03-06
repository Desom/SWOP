package domain.scheduling.schedulers;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.InternalFailureException;
import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.DoesNotExistException;
import domain.scheduling.NoOrdersToBeScheduledException;
import domain.scheduling.order.Order;
import domain.scheduling.order.SingleTaskOrder;
import domain.scheduling.schedulers.algorithm.AssemblyLineSchedulingAlgorithm;

public class AssemblyLineScheduler implements Scheduler{

	public static final int END_OF_DAY = 22;
	public static final int BEGIN_OF_DAY = 6;
	private int overTimeInMinutes;
	private AssemblyLine assemblyLine;
	private ArrayList<AssemblyLineSchedulingAlgorithm> possibleAlgorithms;
	private AssemblyLineSchedulingAlgorithm currentAlgorithm;
	private GregorianCalendar currentTime;
	private OrderHandler orderHandler;
	private ArrayList<ScheduledOrder> schedule;
	private boolean outDated;
	private ArrayList<AssemblyLineSchedulerObserver> observers;

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
	public AssemblyLineScheduler(GregorianCalendar time, ArrayList<AssemblyLineSchedulingAlgorithm> possibleAlgorithms) {
		this.currentTime = (GregorianCalendar) time.clone();
		this.possibleAlgorithms = (ArrayList<AssemblyLineSchedulingAlgorithm>) possibleAlgorithms.clone();
		this.currentAlgorithm = this.possibleAlgorithms.get(0);
		this.outDated = true;
		this.observers = new ArrayList<AssemblyLineSchedulerObserver>();
	}

	/**
	 * Returns the estimated time of completion for the given order, depending on the given list of orders and the given time for de next advance of the assembly line.
	 * 
	 * @param order
	 * 		The order of which the estimated completion time is needed.
	 * @param scheduledOrders
	 * 		A list of ScheduledOrder objects representing the scheduled orders.
	 * @param nextAdvanceTime
	 * 		The time indicating when the next advance of the assembly line will happen.
	 * @return The estimated completion time of the given order.
	 */
	private GregorianCalendar calculateEstimatedCompletionTimeOf(Order order, ArrayList<ScheduledOrder> scheduledOrders, GregorianCalendar nextAdvanceTime){
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
					return nextAdvanceTime;
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
				nextAdvanceTime.add(GregorianCalendar.MINUTE, timeTillAdvance);
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
	 * Returns the estimated time the order on the given position in the given list of ScheduledOrder objects will get off of the assembly line.
	 * 
	 * @param position
	 * 		The position of the order in de given list of ScheduledOrder objects of which the time will be returned.
	 * @param scheduledOrders
	 * 		A list of scheduled orders.
	 * @return
	 * 		The time on which the indicated order in de scheduled list of orders, will get off of the assembly line.
	 */
	private GregorianCalendar timeTillOrderOffAssemblyLine(int position, ArrayList<ScheduledOrder> scheduledOrders) {
		GregorianCalendar simulTime = scheduledOrders.get(position).getScheduledTime();
		LinkedList<Order> assembly;
		if(position < this.getAssemblyLine().getNumberOfWorkstations()-1){
			assembly = this.getAssemblyLine().getAllOrders();
			
			for(int i = position ; i >= 0; i-- ){
				assembly.removeLast();
				assembly.addFirst(scheduledOrders.get(position - i).getScheduledOrder());
			}

		}
		else{
			assembly = new LinkedList<Order>();
			for(int i = this.getAssemblyLine().getNumberOfWorkstations()-1; i >= 0; i--){
				assembly.addFirst(scheduledOrders.get(position-i).getScheduledOrder());
			}
//			assembly.addFirst(scheduledOrders.get(position-1).getScheduledOrder());
//			assembly.addFirst(scheduledOrders.get(position).getScheduledOrder());
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
	@SuppressWarnings("unchecked")
	public GregorianCalendar completionEstimate(Order order){
		if(order == null) throw new IllegalArgumentException("null values are not order");
		ArrayList<ScheduledOrder> scheduledOrders = null;
		try {
			scheduledOrders = this.getSchedule(this.assemblyLine.timeWhenAcceptingOrders());
		} catch (NoOrdersToBeScheduledException e) {
		}
		if(scheduledOrders != null && this.getOrdersToBeScheduled().contains(order)){
			for(ScheduledOrder scheduledOrder:scheduledOrders){
				if(scheduledOrder.getScheduledOrder() == order) return scheduledOrder.getCompletedTime();
			}
		}
		if(this.assemblyLine.getAllOrders().contains(order)){
			if(!this.assemblyLine.stateWhenAcceptingOrders().contains(order)) return this.assemblyLine.timeWhenAcceptingOrders();
			LinkedList<Order> simulator = (LinkedList<Order>) assemblyLine.stateWhenAcceptingOrders().clone();
			int timespent = 0;
			// places  the orders one by one and remove the last one which will be added to result
			ArrayList<ScheduledOrder> orderList = (ArrayList<ScheduledOrder>) scheduledOrders.clone();
			while(true){
				ArrayList<Order> completed = advanceReturnOfBelt(simulator, orderList, assemblyLine);
				if(completed.contains(order))
				{
					GregorianCalendar time = (GregorianCalendar) this.assemblyLine.timeWhenAcceptingOrders();
					time.add(GregorianCalendar.MINUTE, timespent);
					return time;

				}
				timespent += assemblyLine.calculateTimeTillAdvanceFor(simulator);
			}
		}
		throw new IllegalArgumentException("The AssemblyLineScheduler:" + this + " doesn't schedule the given Order:" + order);
	}

	private ArrayList<Order> advanceReturnOfBelt(LinkedList<Order> simulator,
			ArrayList<ScheduledOrder> orderList, AssemblyLine assemblyLine) {
		ArrayList<Order> result = new ArrayList<Order>();
		LinkedList<Order> simulAssembly =  simulator;
		result.add(simulAssembly.removeLast());
		simulAssembly.addFirst(null);
		for(int j= assemblyLine.getNumberOfWorkstations()-1; j>=0;j--){
			try {
				if(j== 0 && simulAssembly.get(0) == null && !orderList.isEmpty()){
					simulAssembly.removeFirst();
					simulAssembly.addFirst(orderList.remove(0).getScheduledOrder());
					j+=2;
				}else
					if(simulAssembly.get(j) != null && !assemblyLine.filterWorkstations(simulAssembly.get(j).getAssemblyprocess()).contains(assemblyLine.selectWorkstationById(j+1))){
						if(j==assemblyLine.getNumberOfWorkstations()-1){
							result.add(simulAssembly.removeLast());
							simulAssembly.addLast(null);
						}
						else{
							if(simulAssembly.get(j+1)==null){
								simulAssembly.remove(j+1);
								simulAssembly.add(j+1, simulAssembly.get(j));
								simulAssembly.remove(j);
								simulAssembly.add(j, null);
								j+=2;
							}
						}
					}
			} catch (DoesNotExistException e) {
				// onmogelijk
			}
		}
		return result;
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
		ArrayList<ScheduledOrder> scheduledOrdersWithExtra = this.currentAlgorithm.scheduleToScheduledOrderList(orders,this.assemblyLine);
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
	public Order getNextOrder(int minutes) throws NoOrdersToBeScheduledException{
		this.addCurrentTime(minutes);
		GregorianCalendar now = this.assemblyLine.timeWhenAcceptingOrders();
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

	public Order getNextOrderSkip() throws NoOrdersToBeScheduledException {
		ArrayList<ScheduledOrder> scheduledOrders = getSchedule(this.getCurrentTime());
		int i = 0;
		while(this.getAssemblyLine() != null
				&& scheduledOrders.get(i).getScheduledOrder() == null){
			i++;
			if(i >= scheduledOrders.size()){
				throw new NoOrdersToBeScheduledException();
			}
		}
		if(scheduledOrders.get(i).getScheduledTime().equals(this.getCurrentTime()))
			return scheduledOrders.get(i).getScheduledOrder();
		if(this.getAssemblyLine() != null && this.getAssemblyLine().isEmpty() && scheduledOrders.get(i).getScheduledTime().get(GregorianCalendar.HOUR_OF_DAY) == AssemblyLineScheduler.BEGIN_OF_DAY){
			this.startNewDay();
			return scheduledOrders.get(i).getScheduledOrder();
		}

		return null;
		//throw new InternalFailureException("The currentAlgorithm didn't schedule an order for now even though he should have.");
	}

	/**
	 * Returns the Order that will be put on the assembly after the given amount
	 * of minutes, but without updating currentTime.
	 * 
	 * @param minutes
	 * 		The amount of minutes it took to complete the tasks in the
	 *      Workstations. (in minutes)
	 * @return The Order that is scheduled to be built after the given amount of minutes.
	 * @throws NoOrdersToBeScheduledException
	 * 		If there are no orders to be scheduled.
	 */
	public Order seeNextOrder(int minutes) throws NoOrdersToBeScheduledException{
		GregorianCalendar futureTime = this.getCurrentTime();
		futureTime.add(GregorianCalendar.MINUTE, minutes);
		ArrayList<ScheduledOrder> scheduledOrders = this.getSchedule(futureTime);
		int i = 0;
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
	 * @param nextAdvanceTime
	 * 		The time on which the assembly line the will advance.
	 * @param stateOfAssemblyLine 
	 * @return The schedule of this assembly line scheduler.
	 * @throws NoOrdersToBeScheduledException
	 * 		If the schedule of this assembly line scheduler is empty.
	 */
	private ArrayList<ScheduledOrder> getSchedule(GregorianCalendar nextAdvanceTime) throws NoOrdersToBeScheduledException {
		if(this.outDated 
				|| this.schedule == null 
				|| this.schedule.size() == 0
				|| !nextAdvanceTime.equals(this.schedule.get(0).getScheduledTime())){
			this.schedule = this.getCurrentAlgorithm().scheduleToScheduledOrderList(this.getOrdersToBeScheduled(), assemblyLine);
			this.outDated = false;
		}
		if(this.getAssemblyLine() != null && !this.schedule.isEmpty() && this.getAssemblyLine().getAllOrders().contains(this.schedule.get(0).getScheduledOrder())){
			this.schedule = this.getCurrentAlgorithm().scheduleToScheduledOrderList(this.getOrdersToBeScheduled(), assemblyLine);
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

		boolean hasOrder = false;
		if(this.schedule != null){
			for(ScheduledOrder schOrder : this.schedule){
				if(schOrder.getScheduledOrder() != null){
					hasOrder = true;
					break;
				}
			}
		}

		if(this.getAssemblyLine() != null && this.getAssemblyLine().isEmpty() && !hasOrder && !this.getOrdersToBeScheduled().isEmpty()){
			this.notifyObservers();
		}
	}

	/**
	 * Returns the orders to be scheduled.
	 * 
	 * @return The orders to be scheduled, empty if this assembly line scheduler has no order handler.
	 */
	@Override
	public ArrayList<Order> getOrdersToBeScheduled() {
		if(this.getOrderHandler() != null){
			ArrayList<Order> orders = this.getOrderHandler().getOrdersFor(this);
			LinkedList<Order> onAssembly = this.getAssemblyLine().getAllOrders();
			orders.removeAll(onAssembly);
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
	public void setSchedulingAlgorithm(AssemblyLineSchedulingAlgorithm algorithm) throws IllegalArgumentException {
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
	public ArrayList<AssemblyLineSchedulingAlgorithm> getPossibleAlgorithms() {
		return possibleAlgorithms;
	}

	/**
	 * Returns the current algorithm of this assembly line scheduler.
	 * 
	 * @return The current algorithm of this assembly line scheduler.
	 */
	public AssemblyLineSchedulingAlgorithm getCurrentAlgorithm(){
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
	public void addCurrentTime(int minutes){
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
	 * Set the order handler.
	 * 
	 * @param orderHandler
	 * 		The orderHandler to be set.
	 * @throws IllegalStateException
	 * 		If this assembly line scheduler is already coupled with an order handler.
	 * @throws IllegalArgumentException
	 * 		If the given order handler is already coupled to another assembly line scheduler.
	 */
	public void setOrderHandler(OrderHandler orderHandler) throws IllegalStateException, IllegalArgumentException {
		if(this.orderHandler != null){
			if(this.orderHandler.hasScheduler(this)){
				throw new IllegalStateException("The AssemblyLineScheduler: " + this.toString() + " is already coupled with an OrderHandler");
			}
		}
		if(!orderHandler.hasScheduler(this)){
			throw new IllegalArgumentException("The given OrderHandler isn't coupled with this AssemblyLineScheduler.");
		}
		this.orderHandler = orderHandler;
	}

	/**
	 * Returns the order handler of this assembly line scheduler.
	 * 
	 * @return The order handler of this assembly line scheduler.
	 */
	private OrderHandler getOrderHandler() {
		return orderHandler;
	}

	/**
	 * Checks if this assembly line scheduler has the means to complete the given order.
	 * 
	 * @param order
	 * 		The order for which will be checked.
	 * @return True if the order can be completed, false otherwise.
	 */
	@Override
	public boolean canScheduleOrder(Order order) {
		return this.getAssemblyLine().canAcceptNewOrders() && this.getAssemblyLine().canDoOrder(order);
	}

	/**
	 * Adds a new observer to this object.
	 * 
	 * @param observer
	 * 		The new observer for this object.
	 */
	public void addObserver(AssemblyLineSchedulerObserver observer) {
		this.observers.add(observer);
	}

	/**
	 * Notifies all observers.
	 */
	private void notifyObservers() {
		for (AssemblyLineSchedulerObserver observer : this.observers)
			observer.update();
	}

	@Override
	public GregorianCalendar getLatestTime() {
		return this.getCurrentTime();
	}

	public boolean canScheduleOrder() {
		return this.assemblyLine.canAcceptNewOrders();
	}
}

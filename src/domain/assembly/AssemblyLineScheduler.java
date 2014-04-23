package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.InternalFailureException;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.order.Order;
import domain.order.OrderManager;

public class AssemblyLineScheduler implements Scheduler{

	public static final int END_OF_DAY = 22;
	public static final int BEGIN_OF_DAY = 6;
	private int overTimeInMinutes; //TODO updaten
	private AssemblyLine assemblyLine;
	private ArrayList<SchedulingAlgorithm> possibleAlgorithms;
	private SchedulingAlgorithm currentAlgorithm;
	private GregorianCalendar currentTime;
	private OrderManager orderManager;
	private ArrayList<ScheduledOrder> schedule;
	private boolean outDated;

	//TODO docs
	//eerste algorithm van de possibleAlgorithms is het default algorithm.
	public AssemblyLineScheduler(GregorianCalendar time, ArrayList<SchedulingAlgorithm> possibleAlgorithms) {
		this.currentTime = (GregorianCalendar) time.clone();
		this.possibleAlgorithms = (ArrayList<SchedulingAlgorithm>) possibleAlgorithms.clone();
		this.currentAlgorithm = this.possibleAlgorithms.get(0);
		this.outDated = true;
	}
	
	//TODO docs
	public GregorianCalendar completionEstimate(Order order){
		//TODO refactoring?
		if(order == null){
			throw new IllegalArgumentException("It is impossible to calculate the completionEstimate with null.");
		}
		
		if(this.getAssemblyLine() == null){
			throw new InternalFailureException("An AssemblyLineScheduler doesn't have an AssemblyLine which is necessary for completionEstimate.");
		}
		
		int time = this.getAssemblyLine().calculateTimeTillAdvanceFor(this.getAssemblyLine().getAllOrders());
		GregorianCalendar futureTime = this.getCurrentTime();
		futureTime.add(GregorianCalendar.MINUTE, time);
		
		//als order al op de AssemblyLine staat
		if(this.getAssemblyLine().getAllOrders().contains(order)){
			ArrayList<ScheduledOrder> scheduledOrders;
			try {
				scheduledOrders = getSchedule(futureTime);
			} catch (NoOrdersToBeScheduledException e) {
				scheduledOrders = null;
			}
			LinkedList<Order> assembly = this.getAssemblyLine().getAllOrders();
			int i = 0;
			while(assembly.contains(order)){
				if(order.equals(assembly.getLast())){
					return futureTime;
				}
				assembly.removeLast();
				if(scheduledOrders != null){
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
		
		
		//als order nog moet worden gescheduled

		ArrayList<ScheduledOrder> scheduledOrders;
		try {
			scheduledOrders = getSchedule(futureTime);
		} catch (NoOrdersToBeScheduledException e) {
			throw new IllegalArgumentException("The AssemblyLineScheduler:" + this + " doesn't schedule the given Order:" + order);
		}

		int position = 0;
		for(; position < scheduledOrders.size(); position++){
			if(order.equals(scheduledOrders.get(position).getScheduledOrder())){
				break;
			}
		}
		if(position >= scheduledOrders.size()){
			//TODO goede exception?
			throw new IllegalArgumentException("The AssemblyLineScheduler:" + this + " doesn't schedule the given Order:" + order);
		}
		GregorianCalendar simulTime = scheduledOrders.get(position).getScheduledTime();
		LinkedList<Order> assembly;
		if(position < 2){
			assembly = this.getAssemblyLine().getAllOrders();
			if(position < 1){
				assembly.removeLast();
				assembly.add(scheduledOrders.get(0).getScheduledOrder());
			}
			assembly.removeLast();
			assembly.add(scheduledOrders.get(1).getScheduledOrder());

		}
		else{
			assembly = new LinkedList<Order>();
			assembly.addFirst(scheduledOrders.get(position-2).getScheduledOrder());
			assembly.addFirst(scheduledOrders.get(position-1).getScheduledOrder());
			assembly.addFirst(scheduledOrders.get(position).getScheduledOrder());
		}
		for(int i = position; i < position + 3; i ++){
			simulTime.add(GregorianCalendar.MINUTE, this.getAssemblyLine().calculateTimeTillAdvanceFor(assembly));
			assembly.remove();
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
	 * Returns the Order that will be put on the AssemblyLine immediately after updating
	 * the currentTime with the given amount of minutes.
	 * 
	 * @param minutes
	 *            The amount of minutes it took to complete the tasks in the Workstations. (in minutes)
	 * @return The Order that is scheduled to be built now.
	 * @throws NoOrdersToBeScheduledException 
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
	 *            The amount of minutes it took to complete the tasks in the
	 *            Workstations. (in minutes)
	 * @return The CarOrder that is scheduled to be built after the given amount of minutes.
	 * @throws NoOrdersToBeScheduledException 
	 */
	protected Order seeNextOrder(int minutes) throws NoOrdersToBeScheduledException{
		GregorianCalendar futureTime = this.getCurrentTime();
		futureTime.add(GregorianCalendar.MINUTE, minutes);
		ArrayList<ScheduledOrder> scheduledOrders = getSchedule(futureTime);
		int i = 0;
		while(this.getAssemblyLine() != null 
				&& this.getAssemblyLine().isEmpty() 
				&& scheduledOrders.get(i).getScheduledOrder() == null){
			i++;
			if(i >= scheduledOrders.size()){
				throw new NoOrdersToBeScheduledException();
			}
		}
		if(scheduledOrders.get(i).getScheduledTime().equals(futureTime)){
			return scheduledOrders.get(i).getScheduledOrder();
		}
		if(this.getAssemblyLine().isEmpty() && scheduledOrders.get(i).getScheduledTime().get(GregorianCalendar.HOUR_OF_DAY) == AssemblyLineScheduler.BEGIN_OF_DAY){
			return scheduledOrders.get(i).getScheduledOrder();
		}
		
		throw new InternalFailureException("The currentAlgorithm didn't schedule an order for now even though he should have.");
		
	}

	/**
	 * @param futureTime
	 * @return
	 * @throws NoOrdersToBeScheduledException 
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
		if(this.getAssemblyLine() != null && this.getAssemblyLine().getAllOrders().contains(this.schedule.get(0).getScheduledOrder())){
			this.schedule = this.getCurrentAlgorithm().scheduleToScheduledOrderList(this.getOrdersToBeScheduled(), futureTime, this);
		}
		
		if(this.schedule.isEmpty()){
			throw new NoOrdersToBeScheduledException();
		}
		return this.schedule;
	}
	
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

	// TODO docs
	public ArrayList<Order> getOrdersToBeScheduled() {
		if(this.getOrderManager() != null){
			ArrayList<Order> orders = this.getOrderManager().getAllUnfinishedOrders();
			LinkedList<Order> onAssembly = this.getAssemblyLine().getAllOrders();
			orders.removeAll(onAssembly);
			return orders;
		}
		return new ArrayList<Order>(); //TODO mss beter null?
	}

	//TODO docs
	public void setSchedulingAlgorithm(SchedulingAlgorithm algorithm){
		if(!this.possibleAlgorithms.contains(algorithm))
			throw new IllegalArgumentException("This SchedulingAlgorithm is not one of the possible SchedulingAlgorithms");
		//TODO goede exception?
		this.currentAlgorithm = algorithm;
	}
	
	//TODO docs
	public void setSchedulingAlgorithmToDefault() {
		this.currentAlgorithm = this.getPossibleAlgorithms().get(0);
	}

	public ArrayList<SchedulingAlgorithm> getPossibleAlgorithms() {
		return possibleAlgorithms;
	}
	
	public SchedulingAlgorithm getCurrentAlgorithm(){
		return this.currentAlgorithm;
	}

	public GregorianCalendar getCurrentTime() {
		return (GregorianCalendar) this.currentTime.clone();
	}

	/**
	 * Adds minutes to the current time of this assembly line scheduler.
	 * 
	 * @param time
	 * 		Advance of time in minutes.
	 */
	//*If a new work day is started (if the resulting current time equals BEGIN_OF_DAY) overtime will be recalculated.
	 
	private void addCurrentTime(int time){
		this.currentTime.add(GregorianCalendar.MINUTE, time);
//		GregorianCalendar futureTime = (GregorianCalendar) this.currentTime.clone();
//		futureTime.add(GregorianCalendar.MINUTE, time);
//		if (futureTime.get(GregorianCalendar.HOUR_OF_DAY) == this.BEGIN_OF_DAY) {
//			// nieuwe overtime berekenen
//		}
	}

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
	
	//TODO docs
	private void updateOverTime() {
		GregorianCalendar time = this.getCurrentTime();
		GregorianCalendar endOfDay = this.getRealEndOfDay();
		if(time.before(endOfDay)){
			this.overTimeInMinutes = 0;
			return;
		}
		
		//TODO is dit ok?
		int newOverTime = 0;
		while(!time.equals(endOfDay)){
			time.add(GregorianCalendar.MINUTE, 1);
			newOverTime++;
		}
		
		this.overTimeInMinutes = newOverTime;
	}

	/**
	 * Gets the end of the day with overtime taken into account.
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


	public void setAssemblyLine(AssemblyLine assemblyLine){
		if(this.assemblyLine != null){
			if(this.assemblyLine.getAssemblyLineScheduler() == this){
				//TODO goede exception?
				throw new IllegalStateException("The AssemblyLineScheduler: " + this.toString() + " is already coupled with an AssemblyLine");
			}
		}
		if(assemblyLine.getAssemblyLineScheduler() != this){
			throw new IllegalArgumentException("The given AssemblyLine is already coupled with a different Scheduler.");
		}
		this.assemblyLine = assemblyLine;
	}
	
	public AssemblyLine getAssemblyLine() {
		return assemblyLine;
	}

	//TODO stom dat deze public is...
	public void setOrderManager(OrderManager orderManager){
		if(this.orderManager != null){
			if(this.orderManager.getScheduler() == this){
				//TODO goede exception?
				throw new IllegalStateException("The AssemblyLineScheduler: " + this.toString() + " is already coupled with an OrderManager");
			}
		}
		if(orderManager.getScheduler() != this){
			throw new IllegalArgumentException("The given OrderManager is already coupled with a different Scheduler.");
		}
		this.orderManager = orderManager;
	}
	
	private OrderManager getOrderManager() {
		return orderManager;
	}
	



}

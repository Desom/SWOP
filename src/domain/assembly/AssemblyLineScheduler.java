package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.algorithm.EfficiencySchedulingAlgorithm;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.order.CarOrder;
import domain.order.Order;
import domain.order.OrderManager;

public class AssemblyLineScheduler implements Scheduler{

	public static final int END_OF_DAY = 22;
	public static final int BEGIN_OF_DAY = 6;
	private int overTimeInMinutes; //TODO updaten
	private AssemblyLine assemblyLine;
	private ArrayList<SchedulingAlgorithm> possibleAlgorithms;
	private SchedulingAlgorithm currentAlgorithm;
	private EfficiencySchedulingAlgorithm algorithmToBeUsed;
	private GregorianCalendar currentTime;
	private OrderManager orderManager;

	//TODO docs
	//eerste algorithm van de possibleAlgorithms is het default algorithm.
	public AssemblyLineScheduler(GregorianCalendar time, ArrayList<SchedulingAlgorithm> possibleAlgorithms) {
		this.currentTime = time;
		this.possibleAlgorithms = possibleAlgorithms;
		this.algorithmToBeUsed = new EfficiencySchedulingAlgorithm(this.possibleAlgorithms.get(0));
	}
	
	//TODO docs
	public GregorianCalendar completionEstimate(Order order){
		//TODO estimated time terug geven voor Orders die al op de assemblyLine staan?
		int time = this.getAssemblyLine().calculateTimeTillAdvanceFor(this.getAssemblyLine().getAllOrders());
		GregorianCalendar futureTime = this.getCurrentTime();
		futureTime.add(GregorianCalendar.MINUTE, time);
		ArrayList<ScheduledOrder> scheduledOrders = this.algorithmToBeUsed.scheduleToScheduledOrderList(this.getOrdersToBeScheduled(), futureTime, this);

		int position = 0;
		for(; position < scheduledOrders.size(); position++){
			if(scheduledOrders.get(position).equals(order)){
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
	 */
	//TODO docs
	protected Order getNextOrder(int minutes){
		this.addCurrentTime(minutes);
		GregorianCalendar now = this.getCurrentTime();
		ArrayList<ScheduledOrder> scheduledOrders = this.algorithmToBeUsed.scheduleToScheduledOrderList(this.getOrdersToBeScheduled(), now, this);
		if(scheduledOrders.get(0).getScheduledTime().equals(this.getCurrentTime())){
			return scheduledOrders.get(0).getScheduledOrder();
		}
		if(this.getAssemblyLine().isEmpty() && scheduledOrders.get(0).getScheduledTime().get(GregorianCalendar.HOUR_OF_DAY) == this.BEGIN_OF_DAY){
			this.startNewDay();
			return scheduledOrders.get(0).getScheduledOrder();
		}
		return null;
		
	}

	/**
	 * Returns the Order that will be put on the assembly after the given amount
	 * of minutes, but without updating currentTime.
	 * 
	 * @param minutes
	 *            The amount of minutes it took to complete the tasks in the
	 *            Workstations. (in minutes)
	 * @return The CarOrder that is scheduled to be built after the given amount of minutes.
	 */
	//TODO docs
	protected Order seeNextOrder(int minutes){
		GregorianCalendar futureTime = this.getCurrentTime();
		futureTime.add(GregorianCalendar.MINUTE, minutes);
		ArrayList<ScheduledOrder> scheduledOrders = this.algorithmToBeUsed.scheduleToScheduledOrderList(this.getOrdersToBeScheduled(), futureTime, this);
		if(scheduledOrders.get(0).getScheduledTime().equals(futureTime)){
			return scheduledOrders.get(0).getScheduledOrder();
		}
		return null;
	}
	

	// TODO docs
	private ArrayList<Order> getOrdersToBeScheduled() {
		ArrayList<Order> orders = this.getOrderManager().getAllUnfinishedOrders();
		LinkedList<Order> onAssembly = this.getAssemblyLine().getAllOrders();
		orders.removeAll(onAssembly);
		return orders;
	}

	//TODO docs
	public void setSchedulingAlgorithm(SchedulingAlgorithm algorithm){
		if(!this.possibleAlgorithms.contains(algorithm))
			throw new IllegalArgumentException("This SchedulingAlgorithm is not one of the possible SchedulingAlgorithms");
		//TODO goede exception?
		this.currentAlgorithm = algorithm;
		this.algorithmToBeUsed.setInnerAlgorithm(this.currentAlgorithm);
	}

	public ArrayList<SchedulingAlgorithm> getPossibleAlgorithms() {
		return possibleAlgorithms;
	}
	
	public SchedulingAlgorithm getCurrentAlgorithm(){
		return this.currentAlgorithm;
	}

	public GregorianCalendar getCurrentTime() {
		return (GregorianCalendar) currentTime.clone();
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
		GregorianCalendar newDay = new GregorianCalendar(
				this.currentTime.get(GregorianCalendar.YEAR),
				this.currentTime.get(GregorianCalendar.MONTH),
				this.currentTime.get(GregorianCalendar.DAY_OF_MONTH),
				this.BEGIN_OF_DAY,
				0,
				0);
		if(newDay.before(this.currentTime))
			newDay.add(GregorianCalendar.DAY_OF_MONTH, 1);
		this.currentTime = newDay;
	}
	
	/**
	 * Gets the end of the day with overtime taken into account.
	 */
	public GregorianCalendar getRealEndOfDay() {
		GregorianCalendar endOfDay = (GregorianCalendar) this.currentTime.clone();
		endOfDay.set(GregorianCalendar.HOUR_OF_DAY, 22);
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

package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

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
	private GregorianCalendar currentTime;
	private OrderManager orderManager;

	//TODO docs
	public AssemblyLineScheduler(GregorianCalendar time, ArrayList<SchedulingAlgorithm> possibleAlgorithms) {
		this.currentTime = time;
		this.possibleAlgorithms = possibleAlgorithms;
	}

	//TODO docs
	//	private ArrayList<CarOrder> schedule(){
	//		return this.currentAlgorithm.schedule(this.generalScheduler.getOrdersFor(this), this);
	//	}

	//TODO docs
	public GregorianCalendar completionEstimate(Order order){
		//TODO estimated time terug geven voor Orders die al op de assemblyLine staan?
		int time = this.getAssemblyLine().calculateTimeTillAdvanceFor(this.getAssemblyLine().getAllOrders());
		GregorianCalendar futureTime = this.getCurrentTime();
		futureTime.add(GregorianCalendar.MINUTE, time);
		ArrayList<ScheduledOrder> scheduledOrders = this.currentAlgorithm.scheduleToScheduledOrderList(this.getOrdersToBeScheduled(), futureTime, this);

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
	public Order getNextOrder(int minutes){
		this.addCurrentTime(minutes);
		GregorianCalendar now = this.getCurrentTime();
		ArrayList<ScheduledOrder> scheduledOrders = this.currentAlgorithm.scheduleToScheduledOrderList(this.getOrdersToBeScheduled(), now, this);
		if(scheduledOrders.get(0).getScheduledTime().equals(this.getCurrentTime())){
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
	public Order seeNextOrder(int minutes){
		GregorianCalendar futureTime = this.getCurrentTime();
		futureTime.add(GregorianCalendar.MINUTE, minutes);
		ArrayList<ScheduledOrder> scheduledOrders = this.currentAlgorithm.scheduleToScheduledOrderList(this.getOrdersToBeScheduled(), futureTime, this);
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
	}

	public ArrayList<SchedulingAlgorithm> getPossibleAlgorithms() {
		return possibleAlgorithms;
	}

	public GregorianCalendar getCurrentTime() {
		return (GregorianCalendar) currentTime.clone();
	}

	//TODO docs
	private void addCurrentTime(int time){
		this.currentTime.add(GregorianCalendar.MINUTE, time);
	}
	
	/**
	 * Gets the end of the day with overtime taken into account.
	 */
	public GregorianCalendar getRealEndOfDay() {
		GregorianCalendar endOfDay = (GregorianCalendar) GregorianCalendar.getInstance();
		endOfDay.set(GregorianCalendar.HOUR_OF_DAY, 22);
		endOfDay.add(GregorianCalendar.MINUTE, - this.overTimeInMinutes);
		return endOfDay;
	}

	public AssemblyLine getAssemblyLine() {
		return assemblyLine;
	}

	private OrderManager getOrderManager() {
		return orderManager;
	}

}

package Main;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class ProductionSchedule {
	//TODO protected
	private static final int ASSEMBLY_DURATION = 3;
	private static final int WORKSTATION_DURATION = 1;
	private static final int BEGIN_WORKDAY = 6;
	private static final int END_WORKDAY = 22;
	private static final int AMOUNT_WORKSTATIONS = 3;
	
	//TODO is er een beter queue? 
	private LinkedList<CarOrder> scheduleQueue;
	private ArrayList<Integer> timeHistory;
	private int overTime; //TODO hoe overTime te weten komen? zelf counter bijhouden?
	
	/**
	 * The constructor for the ProductionSchedule class.
	 * The constructor schedules the CarOrder from carOrderList using the FIFO strategy.
	 * 
	 * @param 	carOrderList
	 * 			The list of carOrders that are initially in the schedule
	 */
	public ProductionSchedule(List<CarOrder> carOrderList){
		this.setOverTime(0);
		this.setTimeHistory(new ArrayList<Integer>());
		Comparator<CarOrder> comparatorFIFO = new Comparator<CarOrder>(){
			public int compare(CarOrder order1, CarOrder order2){
				return order1.getOrderedTime().compareTo(order2.getOrderedTime());
			}
		};
		Collections.sort(carOrderList, comparatorFIFO);
		this.setScheduleQueue(new LinkedList<CarOrder>(carOrderList));
	}
	/**
	 * Calculates an estimated completion date for a specific CarOrder and returns it.
	 * 
	 * @param 	order
	 * 			The CarOrder whose estimated completion date is requested.
	 * @return	A GregorianCalendar representing the estimated completion date of order.
	 */
	public GregorianCalendar completionEstimateCarOrder(CarOrder order){
		int positionInLine = this.getScheduleQueue().indexOf(order);
		GregorianCalendar completionTime = new GregorianCalendar();
		completionTime.add(GregorianCalendar.HOUR_OF_DAY, (positionInLine*WORKSTATION_DURATION) + ASSEMBLY_DURATION);
		return completionTime;
	}
	
	/**
	 * Checks if there is still enough time left today to built another Car within working hours.
	 * 
	 * @return True if there is still enough time left, false otherwise.
	 */
	private boolean checkTimeRequirement() {
		GregorianCalendar begin_today = new GregorianCalendar();
		begin_today.set(GregorianCalendar.HOUR_OF_DAY, BEGIN_WORKDAY);
		GregorianCalendar end_today = new GregorianCalendar();
		end_today.set(GregorianCalendar.HOUR_OF_DAY, (END_WORKDAY - this.getOverTime()));
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar auto_finished = new GregorianCalendar();
		auto_finished.add(GregorianCalendar.HOUR_OF_DAY, AMOUNT_WORKSTATIONS * WORKSTATION_DURATION);
		if(now.before(begin_today))
			return false;
		if(end_today.before(auto_finished))
			return false;
		return true;
	}
	
	/**
	 * Add a new CarOrder to the schedule.
	 * 
	 * @param 	order
	 * 			The CarOrder that has to be scheduled.
	 */
	public void addOrder(CarOrder order){
		this.getScheduleQueue().add(order);
	}
	
	/**
	 * Returns the next CarOrder to be built, but without removing it from the front of the schedule.
	 * 
	 * @return The CarOrder that is scheduled to be built next.
	 */
	public CarOrder seeNextCarOrder(){
		if(this.checkTimeRequirement()){
			return this.getScheduleQueue().element();
		}
		return null;
	}
	
	/**
	 * Returns the next CarOrder to be built and removes it from the front of the schedule.
	 * 
	 * @param 	time
	 * 			The time past since the last time this method was called today.
	 * @return	The CarOrder that is scheduled to be built next.
	 */
	public CarOrder getNextCarOrder(int time){
		this.getTimeHistory().add(time);
		if(this.checkTimeRequirement()){
			return this.getScheduleQueue().remove();
		}
		return null;
	}
	
	private LinkedList<CarOrder> getScheduleQueue() {
		return this.scheduleQueue;
	}

	private void setScheduleQueue(LinkedList<CarOrder> scheduleQueue) {
		this.scheduleQueue = scheduleQueue;
	}

	private ArrayList<Integer> getTimeHistory() {
		return this.timeHistory;
	}

	private void setTimeHistory(ArrayList<Integer> timeHistory) {
		this.timeHistory = timeHistory;
	}

	private int getOverTime() {
		return overTime;
	}

	private void setOverTime(int overTime) {
		this.overTime = overTime;
	}

}

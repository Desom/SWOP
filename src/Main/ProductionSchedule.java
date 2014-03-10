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
	
	private LinkedList<CarOrder> scheduleQueue;
	private ArrayList<Integer> timeHistory;
	private int overTime;
	private int endOfDayCounter;
	
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
		this.resetEndOfDayCounter();
	}
	/**
	 * Calculates an estimated completion date for a specific CarOrder and returns it.
	 * 
	 * @param 	order
	 * 			The CarOrder whose estimated completion date is requested.
	 * @return	A GregorianCalendar representing the estimated completion date of order.
	 * 			Null if it isn't in this schedule.
	 */
	public GregorianCalendar completionEstimateCarOrder(CarOrder order){
		//TODO zeker goed testen...
		int positionInLine = this.getScheduleQueue().indexOf(order);
		if(positionInLine == -1)
			return null;
		
		//de tijd naar boven afronden.
		GregorianCalendar completionTime = new GregorianCalendar();
		completionTime.add(GregorianCalendar.HOUR_OF_DAY, 1);
		completionTime.set(GregorianCalendar.MINUTE, 0);
		completionTime.set(GregorianCalendar.SECOND, 0);
		completionTime.set(GregorianCalendar.MILLISECOND, 0);

		int waitingHours = positionInLine*WORKSTATION_DURATION;
		int nowHour = completionTime.get(GregorianCalendar.HOUR_OF_DAY);
		
		if(nowHour > BEGIN_WORKDAY){
			if(nowHour < END_WORKDAY-2){
				int diffHours = (END_WORKDAY - 2) - nowHour;
				waitingHours -= diffHours;
			}
			completionTime.add(GregorianCalendar.DATE, 1);
		}
		completionTime.set(GregorianCalendar.HOUR_OF_DAY, BEGIN_WORKDAY);
		
		int dayWaitingTime = (END_WORKDAY - 2 - BEGIN_WORKDAY);
		while(waitingHours > dayWaitingTime){
			waitingHours -= dayWaitingTime;
			completionTime.add(GregorianCalendar.DATE, 1);
		}
		completionTime.add(GregorianCalendar.HOUR_OF_DAY, waitingHours + ASSEMBLY_DURATION);
		
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
			this.resetEndOfDayCounter();
			return this.getScheduleQueue().remove();
		}
		this.incEndOfDayCounter();
		if(this.getEndOfDayCounter() == AMOUNT_WORKSTATIONS){
			this.calculateOverTime();
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
		if(overTime > 0)
			this.overTime = overTime;
		else 
			this.overTime = 0;
	}
	private void calculateOverTime() {
		int time = new GregorianCalendar().get(GregorianCalendar.HOUR_OF_DAY) - END_WORKDAY + this.overTime;
		if(time > 0){
			this.setOverTime(time);
		}
		else{
			this.setOverTime(0);
		}
	}
	
	private int getEndOfDayCounter() {
		return endOfDayCounter;
	}
	
	private void incEndOfDayCounter() {
		this.endOfDayCounter = this.endOfDayCounter + 1;
	}
	
	private void resetEndOfDayCounter() {
		this.endOfDayCounter = 0;
	}
}

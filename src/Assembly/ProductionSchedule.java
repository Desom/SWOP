package Assembly;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import Car.CarOrder;


public class ProductionSchedule {
	//TODO protected
	private static final int ASSEMBLY_DURATION = 3;
	private static final int WORKSTATION_DURATION = 1;
	private static final int BEGIN_WORKDAY = 6;
	private static final int END_WORKDAY = 22;
	
	private LinkedList<CarOrder> scheduleQueue;
	private GregorianCalendar currentTime; // tijd wanneer het laatst werd advanced of begin van de dag.
	private GregorianCalendar endWithOverTime; // in minutes
	private CarOrder[] ordersOnAssemblyLine;
	
	/**
	 * The constructor for the ProductionSchedule class.
	 * The constructor schedules the CarOrder from carOrderList using the FIFO strategy.
	 * 
	 * @param 	carOrderList
	 * 			The list of carOrders that are initially in the schedule
	 * @param	currentTime
	 * 			The date at which this ProductionSchedule starts.
	 */
	public ProductionSchedule(List<CarOrder> carOrderList, GregorianCalendar currentTime){
		this.currentTime = currentTime;
		this.initEndWithOverTime();
		Comparator<CarOrder> comparatorFIFO = new Comparator<CarOrder>(){
			public int compare(CarOrder order1, CarOrder order2){
				return order1.getOrderedTime().compareTo(order2.getOrderedTime());
			}
		};
		Collections.sort(carOrderList, comparatorFIFO);
		this.scheduleQueue = new LinkedList<CarOrder>(carOrderList);
		ordersOnAssemblyLine = new CarOrder[3];
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
		//controleren ofdat hij wel op deze schedule staat
		int positionInLine = this.getScheduleQueue().indexOf(order);
		if(positionInLine == -1)
			return null;
		
		GregorianCalendar completionTime = (GregorianCalendar) this.currentTime.clone();
		//als de assemblyLine leeg is kan er elk moment iets worden opgezet, maar wanneer er iets op staat: currentTime == lastAdvancedTime
		if(!this.assemblyIsEmpty()){
			completionTime.add(GregorianCalendar.HOUR_OF_DAY, WORKSTATION_DURATION);
		}
		
		//als de werkdag nog niet is begonnen, beginnen we pas te rekenen bij het begin van de dag.
		if(completionTime.before(getBeginOfWorkday())){
			completionTime = getBeginOfWorkday();
		}
		//we voegen dit toe zodat we weten wanneer we het order vandaag klaar zou zijn indien het nu op de band gaat
		completionTime.add(GregorianCalendar.HOUR_OF_DAY, ASSEMBLY_DURATION);
		//doe dingen als er nog tijd voor de auto zou zijn indien hij vooraan de lijn zou staan.
		if(!completionTime.after(this.endWithOverTime)){
			int advancesToday = this.endWithOverTime.get(GregorianCalendar.HOUR_OF_DAY) - completionTime.get(GregorianCalendar.HOUR_OF_DAY)+1;
			if(positionInLine < advancesToday){
				completionTime.add(GregorianCalendar.HOUR_OF_DAY, positionInLine*WORKSTATION_DURATION);
				return completionTime;
			}

			completionTime.add(GregorianCalendar.HOUR_OF_DAY, (advancesToday-1)*WORKSTATION_DURATION);
			if(completionTime.after(endWithOverTime))
				advancesToday -= 1;
			if(positionInLine == advancesToday){
				completionTime.add(GregorianCalendar.HOUR_OF_DAY, positionInLine*WORKSTATION_DURATION);
				return completionTime;
			}
			positionInLine -= advancesToday;
		}
		
		//set completionTime naar morgen, begin van de dag.
		completionTime.set(GregorianCalendar.HOUR_OF_DAY, BEGIN_WORKDAY);
		completionTime.set(GregorianCalendar.MINUTE, 0);
		completionTime.set(GregorianCalendar.SECOND, 0);
		completionTime.set(GregorianCalendar.MILLISECOND, 0);
		completionTime.add(GregorianCalendar.DAY_OF_YEAR, 1);
		int advancesPerDay = (END_WORKDAY - BEGIN_WORKDAY - ASSEMBLY_DURATION + WORKSTATION_DURATION)/WORKSTATION_DURATION;
		//zolang hij te ver in de wachtrij staat om er die dag op te komen, ga naar de volgende dag.
		while(positionInLine >= advancesPerDay){
			completionTime.add(GregorianCalendar.DAY_OF_YEAR, 1);
			positionInLine -= advancesPerDay;
		}
		completionTime.add(GregorianCalendar.HOUR_OF_DAY, positionInLine*WORKSTATION_DURATION + ASSEMBLY_DURATION);
		return completionTime;
	}

	/**
	 * Checks if there is still enough time left today to built another Car within working hours.
	 * 
	 * @return True if there is still enough time left, false otherwise.
	 */
	private boolean checkTimeRequirement() {
		GregorianCalendar begin_today = getBeginOfWorkday();
		GregorianCalendar end_today = this.endWithOverTime;
		
		GregorianCalendar auto_finished = (GregorianCalendar) this.currentTime.clone();
		auto_finished.add(GregorianCalendar.HOUR_OF_DAY, ASSEMBLY_DURATION);
		if(this.currentTime.before(begin_today))
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
			return this.getScheduleQueue().peek();
		}
		return null;
	}
	
	/**
	 * Returns the next CarOrder to be built and removes it from the front of the schedule.
	 * If this results in making the assemblyLine completely empty, we start again tomorrow.
	 * 
	 * @param 	time
	 * 			The time past since the last time this method was called today. (in minutes)
	 * @return	The CarOrder that is scheduled to be built next.
	 */
	public CarOrder getNextCarOrder(int time){
		this.currentTime.add(GregorianCalendar.MINUTE, time);
		if(this.checkTimeRequirement()){
			CarOrder nextOrder = this.getScheduleQueue().poll();
			this.putOnLine(nextOrder);
			return nextOrder;
		}
		this.putOnLine(null);
		if(this.assemblyIsEmpty()){
			this.handleEndOfDay();
		}
		return null;
	}

	/**
	 * Keep track of which order is on the assemblyLine
	 * 
	 * @param nextOrder
	 * 			The order that is now put on the assemblyLine.
	 */
	private void putOnLine(CarOrder nextOrder) {
		this.ordersOnAssemblyLine[0] = this.ordersOnAssemblyLine[1];
		this.ordersOnAssemblyLine[1] = this.ordersOnAssemblyLine[2];
		this.ordersOnAssemblyLine[2] = nextOrder;
		
	}
	
	/**
	 * Checks if the assemblyLine is currently empty.
	 * 
	 * @return true if empty, false if there is a CarOrder on the assemblyLine
	 */
	private boolean assemblyIsEmpty() {
		for(CarOrder order: this.ordersOnAssemblyLine){
			if(order!=null)
				return false;
		}
		return true;
	}
	
	private LinkedList<CarOrder> getScheduleQueue() {
		return this.scheduleQueue;
	}

	/**
	 * Handles all the things that need to be set when a workday ends.
	 */
	private void handleEndOfDay() {
		this.calculateEndWithOverTime();
		this.setCurrentTimeTomorrow();
	}
	
	/**
	 * Set the currentTime to the begin of tomorrows workday.
	 */
	private void setCurrentTimeTomorrow() {
		this.currentTime.add(GregorianCalendar.DAY_OF_YEAR, 1);
		this.currentTime.set(GregorianCalendar.HOUR_OF_DAY, BEGIN_WORKDAY);
		this.currentTime.set(GregorianCalendar.MINUTE, 0);
		this.currentTime.set(GregorianCalendar.SECOND, 0);
		this.currentTime.set(GregorianCalendar.MILLISECOND, 0);
		
		
	}
	
	/**
	 * Calculate when the tomorrows workday will end considering the overtime done today.
	 */
	private void calculateEndWithOverTime() {
		int extraDay = this.endWithOverTime.get(GregorianCalendar.DAY_OF_YEAR) - this.currentTime.get(GregorianCalendar.DAY_OF_YEAR);
		int extraHour = this.endWithOverTime.get(GregorianCalendar.HOUR_OF_DAY) - this.currentTime.get(GregorianCalendar.HOUR_OF_DAY);
		int extraMinute = this.endWithOverTime.get(GregorianCalendar.MINUTE) - this.currentTime.get(GregorianCalendar.MINUTE);
		int extraSecond = this.endWithOverTime.get(GregorianCalendar.SECOND) - this.currentTime.get(GregorianCalendar.SECOND);
		int extraMilliSecond = this.endWithOverTime.get(GregorianCalendar.MILLISECOND) - this.currentTime.get(GregorianCalendar.MILLISECOND);
		
		boolean overTime = this.endWithOverTime.before(this.currentTime);
		
		this.endWithOverTime.set(GregorianCalendar.HOUR_OF_DAY, END_WORKDAY);
		this.endWithOverTime.set(GregorianCalendar.MINUTE, 0);
		this.endWithOverTime.set(GregorianCalendar.SECOND, 0);
		this.endWithOverTime.set(GregorianCalendar.MILLISECOND, 0);
		this.endWithOverTime.add(GregorianCalendar.DAY_OF_YEAR, 1);

		if(overTime){
			this.endWithOverTime.add(GregorianCalendar.DAY_OF_YEAR, -extraDay);
			this.endWithOverTime.add(GregorianCalendar.HOUR_OF_DAY, -extraHour);
			this.endWithOverTime.add(GregorianCalendar.MINUTE, -extraMinute);
			this.endWithOverTime.add(GregorianCalendar.SECOND, -extraSecond);
			this.endWithOverTime.add(GregorianCalendar.MILLISECOND, -extraMilliSecond);
		}
	}
	
	/**
	 * Returns when today's workday begins
	 * 
	 * @return The date at which today's workday begins
	 */
	private GregorianCalendar getBeginOfWorkday() {
		GregorianCalendar begin_today = (GregorianCalendar) this.currentTime.clone();
		begin_today.set(GregorianCalendar.HOUR_OF_DAY, BEGIN_WORKDAY);
		begin_today.set(GregorianCalendar.MINUTE, 0);
		begin_today.set(GregorianCalendar.SECOND, 0);
		begin_today.set(GregorianCalendar.MILLISECOND, 0);
		return begin_today;
	}
	
	/**
	 * Initializes the date at which today's workday will end, while knowing there isn't any overTime done.
	 */
	private void initEndWithOverTime() {
		GregorianCalendar endWithOverTime = (GregorianCalendar) this.currentTime.clone();
		endWithOverTime.set(GregorianCalendar.HOUR_OF_DAY, END_WORKDAY);
		endWithOverTime.set(GregorianCalendar.MINUTE, 0);
		endWithOverTime.set(GregorianCalendar.SECOND, 0);
		endWithOverTime.set(GregorianCalendar.MILLISECOND, 0);
		if(endWithOverTime.before(currentTime))
			endWithOverTime.add(GregorianCalendar.DAY_OF_YEAR, 1);
		
		this.endWithOverTime = endWithOverTime;
	}
}

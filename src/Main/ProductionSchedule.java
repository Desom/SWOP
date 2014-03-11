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
	private GregorianCalendar currentTime; // tijd wanneer het laatst werd advanced...
	private GregorianCalendar endWithOverTime; // in minutes
	private int endOfDayCounter;
	
	/**
	 * The constructor for the ProductionSchedule class.
	 * The constructor schedules the CarOrder from carOrderList using the FIFO strategy.
	 * 
	 * @param 	carOrderList
	 * 			The list of carOrders that are initially in the schedule
	 * @param	currentTime TODO
	 */
	public ProductionSchedule(List<CarOrder> carOrderList, GregorianCalendar currentTime){
		this.currentTime = currentTime;
		this.endWithOverTime = this.initEndWithOverTime();
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
	
	private boolean assemblyIsEmpty() {
		// TODO Auto-generated method stub
		return true;
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
	 * 
	 * @param 	time
	 * 			The time past since the last time this method was called today. (in minutes)
	 * @return	The CarOrder that is scheduled to be built next.
	 */
	//TODO welke beperking komt hier op?
	public CarOrder getNextCarOrder(int time){
		this.currentTime.add(GregorianCalendar.MINUTE, time);
		if(this.checkTimeRequirement()){
			this.resetEndOfDayCounter();
			return this.getScheduleQueue().poll();
		}
		this.incEndOfDayCounter();
		if(this.getEndOfDayCounter() == AMOUNT_WORKSTATIONS){
			this.calculateEndWithOverTime();
		}
		return null;
	}

	private LinkedList<CarOrder> getScheduleQueue() {
		return this.scheduleQueue;
	}

	private void setScheduleQueue(LinkedList<CarOrder> scheduleQueue) {
		this.scheduleQueue = scheduleQueue;
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
	 * @return
	 */
	private GregorianCalendar getBeginOfWorkday() {
		GregorianCalendar begin_today = (GregorianCalendar) this.currentTime.clone();
		begin_today.set(GregorianCalendar.HOUR_OF_DAY, BEGIN_WORKDAY);
		begin_today.set(GregorianCalendar.MINUTE, 0);
		begin_today.set(GregorianCalendar.SECOND, 0);
		begin_today.set(GregorianCalendar.MILLISECOND, 0);
		return begin_today;
	}

	private GregorianCalendar initEndWithOverTime() {
		GregorianCalendar endWithOverTime = (GregorianCalendar) this.currentTime.clone();
		endWithOverTime.set(GregorianCalendar.HOUR_OF_DAY, END_WORKDAY);
		endWithOverTime.set(GregorianCalendar.MINUTE, 0);
		endWithOverTime.set(GregorianCalendar.SECOND, 0);
		endWithOverTime.set(GregorianCalendar.MILLISECOND, 0);
		if(endWithOverTime.before(currentTime))
			this.endWithOverTime.add(GregorianCalendar.DAY_OF_YEAR, 1);
		
		return endWithOverTime;
	}
}

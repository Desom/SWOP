import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class ProductionSchedule {
	
	private static final int ASSEMBLY_DURATION = 3;
	private static final int WORKSTATION_DURATION = 1;
	private static final int BEGIN_WORKDAY = 6;
	private static final int END_WORKDAY = 22;
	private static final int AMOUNT_WORKSTATIONS = 3;
	
	//TODO is er een beter queue? 
	private LinkedList<CarOrder> scheduleQueue;
	private ArrayList<Integer> timeHistory;
	private int overTime;
	
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

	public GregorianCalendar completionEstimateCarOrder(CarOrder order){
		int positionInLine = this.getScheduleQueue().indexOf(order);
		GregorianCalendar completionTime = new GregorianCalendar();
		completionTime.add(GregorianCalendar.HOUR_OF_DAY, (positionInLine*WORKSTATION_DURATION) + ASSEMBLY_DURATION);
		return completionTime;
	}
	
	public CarOrder seeNextCarOrder(){
		if(this.checkTimeRequirement()){
			return this.getScheduleQueue().element();
		}
		return null;
	}
	
	// FIXME kan de mogelijkheid creeren waarbij een future status een auto wel toelaat en de advanceLine niet !!
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

	public void addOrder(CarOrder order){
		this.getScheduleQueue().add(order);
	}
	
	
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

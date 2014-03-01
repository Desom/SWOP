import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class ProductionSchedule {
	
	private static final int ASSEMBLY_DURATION = 3;
	//TODO kies de juist queue 
	private LinkedList<CarOrder> scheduleQueue;
	//TODO kies juiste tijd type, mss zelf dataType maken?
	// wat gebeurt hier mee?
	private ArrayList<Time2> timeHistory;
	
	public ProductionSchedule(List<CarOrder> carOrderList){
		Comparator<CarOrder> comparatorFIFO = new Comparator<CarOrder>(){
			public int compare(CarOrder order1, CarOrder order2){
				// TODO hangt van type van tijd
				order1.getOrderedTime().compareTo(order2.getOrderedTime());
			}
		};
		Collections.sort(carOrderList, comparatorFIFO);
		this.setScheduleQueue(new LinkedList<CarOrder>(carOrderList));
	}

	public Time2 completionEstimateCarOrder(CarOrder order){
		int positionInLine = this.getScheduleQueue().indexOf(order);
		Time2 nu = Time2.getCurrentTime();
		Time2 completionTime = nu.toTheFuture(positionInLine + ASSEMBLY_DURATION);
		return completionTime;
	}
	
	public CarOrder seeNextCarOrder(){
		return this.getScheduleQueue().element();
		
	}
	
	public void addOrder(CarOrder order){
		this.getScheduleQueue().add(order);
	}
	
	
	public CarOrder getNextCarOrder(Time2 time){
		this.getTimeHistory().add(time);
		return this.getScheduleQueue().remove();
	}
	
	private LinkedList<CarOrder> getScheduleQueue() {
		return this.scheduleQueue;
	}

	private void setScheduleQueue(LinkedList<CarOrder> scheduleQueue) {
		this.scheduleQueue = scheduleQueue;
	}

	private ArrayList<Time2> getTimeHistory() {
		return this.timeHistory;
	}

	private void setTimeHistory(ArrayList<Time2> timeHistory) {
		this.timeHistory = timeHistory;
	}

}

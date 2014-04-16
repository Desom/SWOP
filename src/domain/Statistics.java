package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import domain.order.Order;
import domain.order.OrderManager;

public class Statistics {

	private LinkedHashMap<Integer,LinkedList<Order>> dailyMapping = new LinkedHashMap<Integer,LinkedList<Order>>();
	private final OrderManager orderManager;

	public Statistics(OrderManager om){
		this.orderManager = om;
	}
	
	/**
	 * Get the median of the amount of cars that are produced per day.
	 * 
	 * @return The median amount of cars produced per day
	 */
	public int getMedianCarsPerDay(){
		LinkedList<Integer> carsPerDay = getCarsPerDay();
		Collections.sort(carsPerDay);
		if(carsPerDay.size() == 0){
			return 0;
		}else{
			return carsPerDay.get((int) Math.floor(carsPerDay.size()/2));
		}
	}
	
	/**
	 * Get the average of the amount of cars that are produced per day.
	 * 
	 * @return The average amount of cars produced per day
	 */
	public int getAverageCarsPerDay(){
		LinkedList<Integer> carsPerDay = getCarsPerDay();
		if(carsPerDay.size() == 0){
			return 0;
		}
		int average = 0;
		for(int i : carsPerDay){
			average += i;
		}
		average /= carsPerDay.size();
		return average;
	}
	
	/**
	 * Get the median delay time of all cars ever made. (Only delays greater than zero are taken into account).
	 * 
	 * @return The median delay time of all cars ever made.
	 */
	public int getMedianDelay(){
		LinkedList<Integer> delays = getDelays();
		Collections.sort(delays);
		if(delays.size() == 0){
			return 0;
		}else{
			return delays.get((int) Math.floor(delays.size()/2));
		}
	}
	
	/**
	 * Get the average delay time of all cars ever made. (Only delays greater than zero are taken into account).
	 * 
	 * @return The average delay time of all cars ever made.
	 */
	public int getAverageDelay(){
		LinkedList<Integer> delays = getDelays();
		if(delays.size() == 0){
			return 0;
		}
		int average = 0;
		for(int i : delays){
			average += i;
		}
		average /= delays.size();
		return average;
	}
	
	/**
	 * Get the amount of cars that were completed yesterday.
	 * 
	 * @return The amount of cars completed yesterday.
	 */
	public int getAmountOfCars1DayAgo(){
		int yesterday = orderManager.getScheduler().getCurrentTime().get(GregorianCalendar.DAY_OF_YEAR) -1;
		if(dailyMapping.get(yesterday) == null){
			return 0;
		}
		return dailyMapping.get(yesterday).size();
	}

	/**
	 * Get the amount of cars that were completed 2 days ago.
	 * 
	 * @return The amount of cars completed 2 days ago.
	 */
	public int getAmountOfCars2DaysAgo(){
		int day = orderManager.getScheduler().getCurrentTime().get(GregorianCalendar.DAY_OF_YEAR) -2;
		if(dailyMapping.get(day) == null){
			return 0;
		}
		return dailyMapping.get(day).size();
	}

	/**
	 * Get the delay time of the last car that had a delay.
	 * 
	 * @return the delay time of the last car that had a delay.
	 */
	public int getLastDelay(){
		LinkedList<Order> list = sortOrdersOnTime();
		Collections.reverse(list);
		for(Order o : list){
			if(o.getDelay() > 0){
				return o.getDelay();
			}
		}
		return -1;
	}
	
	/**
	 * Get the day of the last time there was a delay. Null if there was none
	 * 
	 * @return the the day of the last time there was a delay.
	 */
	public GregorianCalendar getLastDelayDay(){
		LinkedList<Order> list = sortOrdersOnTime();
		Collections.reverse(list);
		for(Order o : list){
			if(o.getDelay() > 0){
				return o.getDeliveredTime();
			}
		}
		return null;
	}

	/**
	 * Get the delay time of the second to last car that had a delay.
	 * 
	 * @return the delay time of the second to last car that had a delay.
	 */
	public int getSecondToLastDelay(){
		LinkedList<Order> list = sortOrdersOnTime();
		Collections.reverse(list);
		boolean found = false;
		for(Order o : list){
			if(o.getDelay() > 0){
				if(found){
					return o.getDelay();					
				}
				found = true;
			}
		}
		return -1;
	}
	
	/**
	 * Get the day of the second to last time there was a delay. null if there was none
	 * 
	 * @return the the day of the second to last time there was a delay.
	 */
	public GregorianCalendar getSecondToLastDelayDay(){
		LinkedList<Order> list = sortOrdersOnTime();
		Collections.reverse(list);
		boolean found = false;
		for(Order o : list){
			if(o.getDelay() > 0){
				if(found){
					return o.getDeliveredTime();					
				}
				found = true;
			}
		}
		return null;
	}
	
	/**
	 * Call this method to notify the statistics of changes. It will then gather information and update itself.
	 */
	public void update(){
		ArrayList<Order> orderList = this.orderManager.getAllCompletedOrders();
		for(Order o : orderList){
			// alleen orders toevoegen van de vorige dagen, niet vandaag
			if(o.getDeliveredTime().get(GregorianCalendar.DAY_OF_YEAR) != this.orderManager.getScheduler().getCurrentTime().get(GregorianCalendar.DAY_OF_YEAR)){
				LinkedList<Order> dayList = dailyMapping.get(o.getDeliveredTime().get(GregorianCalendar.DAY_OF_YEAR));
				if(dayList == null){
					dayList = new LinkedList<Order>();
					dailyMapping.put(o.getDeliveredTime().get(GregorianCalendar.DAY_OF_YEAR), dayList);
				}
				dayList.add(o);
			}
		}
	}
	
	private LinkedList<Integer> getCarsPerDay(){
		LinkedList<Integer> carsPerDay = new LinkedList<Integer>();
		for(LinkedList<Order> list : this.dailyMapping.values()){
			carsPerDay.add(list.size());
		}
		return carsPerDay;
	}
	
	private LinkedList<Integer> getDelays(){
		LinkedList<Integer> delays = new LinkedList<Integer>();
		for(LinkedList<Order> list : this.dailyMapping.values()){
			for(Order o : list){
				if(o.getDelay() > 0){
					delays.add(o.getDelay());
				}
			}
		}
		return delays;
	}
	
	private LinkedList<Order> sortOrdersOnTime(){
		LinkedList<Order> sorted = new LinkedList<Order>();
		for(LinkedList<Order> list : this.dailyMapping.values()){
			for(Order o : list){
				if(o.getDelay() > 0){
					sorted.add(o);
				}
			}
		}
		Collections.sort(sorted, new OrderComparator());
		return sorted;
	}
	
	
	class OrderComparator implements Comparator<Order> {
	    @Override
	    public int compare(Order a, Order b) {
	       return a.getDeliveredTime().compareTo(b.getDeliveredTime());
	    }
	}

}

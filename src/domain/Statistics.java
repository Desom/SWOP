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

	private LinkedHashMap<Integer,LinkedList<Order>> dailyMapping = null;
	private final OrderManager orderManager;

	public Statistics(OrderManager om){
		this.orderManager = om;
		this.update();
	}

	/**
	 * Get the median of the amount of cars that are produced per day.
	 * 
	 * @return The median amount of cars produced per day
	 */
	public int getMedianCarsPerDay(){
		update();
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
		update();
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
		update();
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
		update();
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
		update();
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
		update();
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
		update();
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
		update();
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
		update();
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
		update();
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
	private void update(){
		ArrayList<Order> orderList = this.orderManager.getAllCompletedOrders();
		dailyMapping = new LinkedHashMap<Integer,LinkedList<Order>>();
		for(Order o : orderList){
			LinkedList<Order> dayList = dailyMapping.get(o.getDeliveredTime().get(GregorianCalendar.DAY_OF_YEAR));
			if(dayList == null){
				dayList = new LinkedList<Order>();
				dailyMapping.put(o.getDeliveredTime().get(GregorianCalendar.DAY_OF_YEAR), dayList);
			}
			dayList.add(o);
		}
	}

	private LinkedList<Integer> getCarsPerDay(){
		LinkedList<Integer> carsPerDay = new LinkedList<Integer>();
		LinkedList<LinkedList<Order>> l = new LinkedList<LinkedList<Order>>();
		for(LinkedList<Order> list : this.dailyMapping.values()){
				l.add(list);
		}
		l.remove(this.dailyMapping.get(this.orderManager.getScheduler().getCurrentTime().get(GregorianCalendar.DAY_OF_YEAR)));
		for(LinkedList<Order> list : l){
			carsPerDay.add(list.size());
		}
		return carsPerDay;
	}

	private LinkedList<Integer> getDelays(){
		LinkedList<Integer> delays = new LinkedList<Integer>();
		for(LinkedList<Order> list : this.dailyMapping.values()){
			for(Order o : list){
				if(o.getCarOrderID() == 83){
				}
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
	
	/**
	 * A customized comparator class to compare car orders on delivered time.
	 */
	class OrderComparator implements Comparator<Order> {
		@Override
		public int compare(Order a, Order b) {
			return a.getDeliveredTime().compareTo(b.getDeliveredTime());
		}
	}

}

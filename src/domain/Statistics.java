package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;

public class Statistics {

	private ArrayList<Integer> carsPerDay = new ArrayList<Integer>();
	private ArrayList<Integer> delays = new ArrayList<Integer>();
	private int carsToday = 0;
	private int cars1DayAgo = -1;
	private int cars2DaysAgo = -1;
	private int lastDelay = -1;
	private GregorianCalendar lastDelayDay = null;
	private int secondToLastDelay = -1;
	private GregorianCalendar secondToLastDelayDay = null;

	/**
	 * Call this method when the current day has ended to update the statistics accordingly.
	 */
	public void endDay(){
		carsPerDay.add(carsToday);
		cars2DaysAgo = cars1DayAgo;
		cars1DayAgo = carsToday;
		carsToday = 0;
	}
	
	/**
	 * Call this method to inform the statistics that a car has been completed today.
	 */
	public void carCompleted(){
		carsToday++;
	}
	
	/**
	 * Call this method to inform the statistics of the delay a car accumulated.
	 * Only positive delays will be registered.
	 * 
	 * @param delay
	 * 				The delay (in minutes) 
	 * @param day
	 * 				A gregorianCalendar to specify the date at which this delay occurred.
	 */
	public void addDelay(int delay, GregorianCalendar day ){
		if(delay>0){
			delays.add(delay);
			secondToLastDelay =lastDelay;
			secondToLastDelayDay = lastDelayDay;
			lastDelay = delay;
			lastDelayDay = day;
		}
	}

	/**
	 * Get the median of the amount of cars that are produced per day.
	 * 
	 * @return The median amount of cars produced per day
	 */
	public int getMedianCarsPerDay(){
		Collections.sort(carsPerDay);
		if(carsPerDay.size() == 0){
			return -1;
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
		if(carsPerDay.size() == 0){
			return -1;
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
		Collections.sort(delays);
		if(delays.size() == 0){
			return -1;
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
		if(delays.size() == 0){
			return -1;
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
		return cars1DayAgo;
	}

	/**
	 * Get the amount of cars that were completed 2 days ago.
	 * 
	 * @return The amount of cars completed 2 days ago.
	 */
	public int getAmountOfCars2DaysAgo(){
		return cars2DaysAgo;
	}

	/**
	 * Get the delay time of the last car that had a delay.
	 * 
	 * @return the delay time of the last car that had a delay.
	 */
	public int getLastDelay(){
		return lastDelay;
	}
	
	/**
	 * Get the day of the last time there was a delay.
	 * 
	 * @return the the day of the last time there was a delay.
	 */
	public GregorianCalendar getLastDelayDay(){
		return lastDelayDay;
	}

	/**
	 * Get the delay time of the second to last car that had a delay.
	 * 
	 * @return the delay time of the second to last car that had a delay.
	 */
	public int getSecondToLastDelay(){
		return secondToLastDelay;
	}
	
	/**
	 * Get the day of the second to last time there was a delay.
	 * 
	 * @return the the day of the second to last time there was a delay.
	 */
	public GregorianCalendar getSecondToLastDelayDay(){
		return secondToLastDelayDay;
	}

}

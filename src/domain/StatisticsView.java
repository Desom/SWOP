package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;

public class StatisticsView {

	private final ArrayList<Integer> carsPerDay;
	private final ArrayList<Integer> delays;
	private final int cars1DayAgo;
	private final int cars2DaysAgo;
	private final int lastDelay;
	private final GregorianCalendar lastDelayDay;
	private final int secondToLastDelay;
	private final GregorianCalendar secondToLastDelayDay;
	
	
	/**
	 * Generate a new View for Statistics
	 */
	public StatisticsView(ArrayList<Integer> carsPerDay, ArrayList<Integer> delays, int cars1DayAgo, int cars2DaysAgo, 
			int lastDelay, GregorianCalendar lastDelayDay, int secondToLastDelay, GregorianCalendar secondToLastDelayDay){
		this.carsPerDay = carsPerDay;
		this.delays = delays;
		this.cars1DayAgo = cars1DayAgo;
		this.cars2DaysAgo = cars2DaysAgo;
		this.lastDelay = lastDelay;
		this.lastDelayDay = lastDelayDay;
		this.secondToLastDelay = secondToLastDelay;
		this.secondToLastDelayDay = secondToLastDelayDay;
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

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

	
	public void endDay(){
		carsPerDay.add(carsToday);
		cars2DaysAgo = cars1DayAgo;
		cars1DayAgo = carsToday;
		carsToday = 0;
	}
	
	public void carCompleted(){
		carsToday++;
	}
	
	public void addDelay(int delay, GregorianCalendar day ){
		delays.add(delay);
		secondToLastDelay =lastDelay;
		secondToLastDelayDay = lastDelayDay;
		lastDelay = delay;
		lastDelayDay = day;
	}

	public int getMedianCarsPerDay(){
		Collections.sort(carsPerDay);
		if(carsPerDay.size() == 0){
			return -1;
		}else{
			return carsPerDay.get((int) Math.floor(carsPerDay.size()/2));
		}
	}

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

	public int getMedianDelay(){
		Collections.sort(delays);
		if(delays.size() == 0){
			return -1;
		}else{
			return delays.get((int) Math.floor(delays.size()/2));
		}
	}
	
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

	public int getAmountOfCars1DayAgo(){
		return cars1DayAgo;
	}

	public int getAmountOfCars2DaysAgo(){
		return cars2DaysAgo;
	}

	public int getLastDelay(){
		return lastDelay;
	}
	
	public GregorianCalendar getLastDelayDay(){
		return lastDelayDay;
	}

	public int getSecondToLastDelay(){
		return secondToLastDelay;
	}
	
	public GregorianCalendar getSecondToLastDelayDay(){
		return secondToLastDelayDay;
	}

}

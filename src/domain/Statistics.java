package domain;

import java.util.ArrayList;
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
	 * Call this method when the current day has ended to update the statistics
	 * accordingly.
	 */
	public void endDay() {
		carsPerDay.add(carsToday);
		cars2DaysAgo = cars1DayAgo;
		cars1DayAgo = carsToday;
		carsToday = 0;
	}

	/**
	 * Call this method to inform the statistics that a car has been completed
	 * today.
	 */
	public void carCompleted() {
		carsToday++;
	}

	/**
	 * Call this method to inform the statistics of the delay a car accumulated.
	 * Only positive delays will be registered.
	 * 
	 * @param delay
	 *            The delay (in minutes)
	 * @param day
	 *            A gregorianCalendar to specify the date at which this delay
	 *            occurred.
	 */
	public void addDelay(int delay, GregorianCalendar day) {
		if (delay > 0) {
			delays.add(delay);
			secondToLastDelay = lastDelay;
			secondToLastDelayDay = lastDelayDay;
			lastDelay = delay;
			lastDelayDay = day;
		}
	}

	/**
	 * Generate a view for this statistics object. This view has all kinds of getters to get a better overview of the data.
	 * 
	 * @return a view on this statistics object.
	 */
	public StatisticsView getView() {
		return new StatisticsView(carsPerDay, delays, cars1DayAgo, cars2DaysAgo, lastDelay, lastDelayDay, secondToLastDelay, secondToLastDelayDay);
	}

}

package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.order.CarOrder;
import domain.order.Order;

public class AssemblyLineScheduler implements Scheduler{

	public static final int END_OF_DAY = 22;
	public static final int BEGIN_OF_DAY = 6;
	private AssemblyLine assemblyLine;
	private ArrayList<SchedulingAlgorithm> possibleAlgorithms;
	private SchedulingAlgorithm currentAlgorithm;
	private GregorianCalendar currentTime;

	 //TODO docs
	public AssemblyLineScheduler(GregorianCalendar time) {
		this.currentTime = time;
	}

	 //TODO docs
//	private ArrayList<CarOrder> schedule(){
//		return this.currentAlgorithm.schedule(this.generalScheduler.getOrdersFor(this), this);
//	}

	 //TODO docs
	public GregorianCalendar completionEstimate(Order order){
		//TODO
		return null;
	}

	/**
	 * Returns the next CarOrder to be built and removes it from the front of the schedule.
	 * 
	 * @param time
	 * 		The time past since the last time this method was called today. (in minutes)
	 * @return	The CarOrder that is scheduled to be built next.
	 */
	//TODO docs
	public CarOrder getNextCarOrder(int time){
		//TODO
		return null;
	}
	
	/**
	 * Returns the next CarOrder to be built, but without removing it from the front of the schedule.
	 * 
	 * @param time
	 * 		The time past since the last time getNextCarOrder was called today. (in minutes)
	 * @return The CarOrder that is scheduled to be built next.
	 */
	//TODO docs
	public CarOrder seeNextCarOrder(int time){
		//TODO
		return null;
	}

	//TODO docs
	public void setSchedulingAlgorithm(SchedulingAlgorithm algorithm){
		if(!this.possibleAlgorithms.contains(algorithm))
			throw new IllegalArgumentException("This SchedulingAlgorithm is not one of the possible SchedulingAlgorithms");
		//TODO goede exception?
		this.currentAlgorithm = algorithm;
	}

	public ArrayList<SchedulingAlgorithm> getPossibleAlgorithms() {
		return possibleAlgorithms;
	}

	public GregorianCalendar getCurrentTime() {
		return (GregorianCalendar) currentTime.clone();
	}

	private void addCurrentTime(int time){
		this.currentTime.add(GregorianCalendar.MINUTE, time);
	}

	public AssemblyLine getAssemblyLine() {
		return assemblyLine;
	}
	
}

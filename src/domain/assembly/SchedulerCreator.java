package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.assembly.algorithm.SchedulingAlgorithm;

public class SchedulerCreator {
	
	private ArrayList<SchedulingAlgorithm> possibleAlgorithms;
	
	public SchedulerCreator() {
		this.possibleAlgorithms = new ArrayList<SchedulingAlgorithm>();
		// TODO algoritme lijst aanvullen
	}
	
	public AssemblyLineScheduler createAssemblyLineScheduler(GregorianCalendar startTime) {
		return new AssemblyLineScheduler(startTime, this.possibleAlgorithms);
	}
	
	public FactoryScheduler createFactoryScheduler() {
		// TODO
		return null;
	}
}

package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.assembly.algorithm.AssemblyLineSchedulingAlgorithm;
import domain.assembly.algorithm.EfficiencySchedulingAlgorithm;
import domain.assembly.algorithm.FIFOSchedulingAlgorithm;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.assembly.algorithm.SpecificationBatchSchedulingAlgorithm;

public class SchedulerCreator {
	
	private ArrayList<AssemblyLineSchedulingAlgorithm> possibleAlgorithms;
	
	public SchedulerCreator() {
		this.possibleAlgorithms = new ArrayList<AssemblyLineSchedulingAlgorithm>();
		this.possibleAlgorithms.add(new EfficiencySchedulingAlgorithm(new FIFOSchedulingAlgorithm()));
		this.possibleAlgorithms.add(new EfficiencySchedulingAlgorithm(new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm())));
		// TODO nieuwe lijst voor FactorySchedulers + nieuwe verdeling
	}
	
	public AssemblyLineScheduler createAssemblyLineScheduler(GregorianCalendar startTime) {
		return new AssemblyLineScheduler(startTime, this.possibleAlgorithms);
	}
	
	public FactoryScheduler createFactoryScheduler() {
		return new FactoryScheduler();
	}
}

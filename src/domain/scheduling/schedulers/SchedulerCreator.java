package domain.scheduling.schedulers;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.scheduling.schedulers.algorithm.AlgorithmCreatorInterface;
import domain.scheduling.schedulers.algorithm.AssemblyLineSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.FactorySchedulingAlgorithm;

public class SchedulerCreator implements SchedulerCreatorInterface {
	
	private ArrayList<AssemblyLineSchedulingAlgorithm> possibleAssemblyAlgorithms;
	private AlgorithmCreatorInterface algorithmCreator;
	
	/**
	 * Constructor of SchedulerCreator.
	 * 
	 * @param algorithmCreator
	 * 		The creator for algorithms.
	 */
	public SchedulerCreator(AlgorithmCreatorInterface algorithmCreator) {
		this.algorithmCreator = algorithmCreator;
		
		//dit gaat aangezien het steeds dezelfde efficiency kan zijn, (specBatch is veranderlijk door de configuration)
		this.possibleAssemblyAlgorithms = new ArrayList<AssemblyLineSchedulingAlgorithm>();
		this.possibleAssemblyAlgorithms.add(this.algorithmCreator.makeEfficiency(this.algorithmCreator.makeSameOrder()));

	}
	
	@Override
	public AssemblyLineScheduler createAssemblyLineScheduler(GregorianCalendar startTime) {
		return new AssemblyLineScheduler(startTime, this.possibleAssemblyAlgorithms);
	}
	
	@Override
	public FactoryScheduler createFactoryScheduler(ArrayList<AssemblyLineScheduler> alsList) {
		ArrayList<FactorySchedulingAlgorithm> possibleFactoryAlgorithms = new ArrayList<FactorySchedulingAlgorithm>();
		possibleFactoryAlgorithms.add(this.algorithmCreator.makeBasic(this.algorithmCreator.makeFIFO()));
		possibleFactoryAlgorithms.add(this.algorithmCreator.makeBasic(this.algorithmCreator.makeSpecBatch(this.algorithmCreator.makeFIFO())));
		
		return new FactoryScheduler(alsList, possibleFactoryAlgorithms);
	}
}

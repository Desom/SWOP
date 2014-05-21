package domain.scheduling.schedulers;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.assembly.algorithm.AlgorithmCreatorInterface;
import domain.assembly.algorithm.AssemblyLineSchedulingAlgorithm;
import domain.assembly.algorithm.FactorySchedulingAlgorithm;

public class SchedulerCreator implements SchedulerCreatorInterface {
	
	private ArrayList<AssemblyLineSchedulingAlgorithm> possibleAssemblyAlgorithms;
	private AlgorithmCreatorInterface algorithmCreator;
	
	public SchedulerCreator(AlgorithmCreatorInterface algorithmCreator) {
		this.algorithmCreator = algorithmCreator;
		
		//dit gaat aangezien het steeds dezelfde efficiency kan zijn, (specBatch is veranderlijk door de configuration)
		this.possibleAssemblyAlgorithms = new ArrayList<AssemblyLineSchedulingAlgorithm>();
		this.possibleAssemblyAlgorithms.add(this.algorithmCreator.makeEfficiency(this.algorithmCreator.makeSameOrder()));

	}
	
	/* (non-Javadoc)
	 * @see domain.assembly.SchedulerCreatorInterface#createAssemblyLineScheduler(java.util.GregorianCalendar)
	 */
	@Override
	public AssemblyLineScheduler createAssemblyLineScheduler(GregorianCalendar startTime) {
		return new AssemblyLineScheduler(startTime, this.possibleAssemblyAlgorithms);
	}
	
	/* (non-Javadoc)
	 * @see domain.assembly.SchedulerCreatorInterface#createFactoryScheduler(java.util.ArrayList)
	 */
	@Override
	public FactoryScheduler createFactoryScheduler(ArrayList<AssemblyLineScheduler> alsList) {
		ArrayList<FactorySchedulingAlgorithm> possibleFactoryAlgorithms = new ArrayList<FactorySchedulingAlgorithm>();
		possibleFactoryAlgorithms.add(this.algorithmCreator.makeBasic(this.algorithmCreator.makeFIFO()));
		possibleFactoryAlgorithms.add(this.algorithmCreator.makeBasic(this.algorithmCreator.makeSpecBatch(this.algorithmCreator.makeFIFO())));
		
		return new FactoryScheduler(alsList, possibleFactoryAlgorithms);
	}
}

package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.assembly.algorithm.AlgorithmCreator;
import domain.assembly.algorithm.AssemblyLineSchedulingAlgorithm;
import domain.assembly.algorithm.BasicSchedulingAlgorithm;
import domain.assembly.algorithm.EfficiencySchedulingAlgorithm;
import domain.assembly.algorithm.FIFOSchedulingAlgorithm;
import domain.assembly.algorithm.FactorySchedulingAlgorithm;
import domain.assembly.algorithm.SameOrderSchedulingAlgorithm;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.assembly.algorithm.SpecificationBatchSchedulingAlgorithm;

public class SchedulerCreator {
	
	private ArrayList<AssemblyLineSchedulingAlgorithm> possibleAssemblyAlgorithms;
	private AlgorithmCreator algorithmCreator;
	
	public SchedulerCreator(AlgorithmCreator algorithmCreator) {
		this.algorithmCreator = algorithmCreator;
		
		//dit gaat aangezien het steeds dezelfde efficiency kan zijn, (specBatch is veranderlijk door de configuration)
		this.possibleAssemblyAlgorithms = new ArrayList<AssemblyLineSchedulingAlgorithm>();
		this.possibleAssemblyAlgorithms.add(this.algorithmCreator.makeEfficiency(this.algorithmCreator.makeSameOrder()));

	}
	
	public AssemblyLineScheduler createAssemblyLineScheduler(GregorianCalendar startTime) {
		return new AssemblyLineScheduler(startTime, this.possibleAssemblyAlgorithms);
	}
	
	public FactoryScheduler createFactoryScheduler(ArrayList<AssemblyLineScheduler> alsList) {
		ArrayList<FactorySchedulingAlgorithm> possibleFactoryAlgorithms = new ArrayList<FactorySchedulingAlgorithm>();
		possibleFactoryAlgorithms.add(this.algorithmCreator.makeBasic(this.algorithmCreator.makeFIFO()));
		possibleFactoryAlgorithms.add(this.algorithmCreator.makeBasic(this.algorithmCreator.makeSpecBatch(this.algorithmCreator.makeFIFO())));
		
		return new FactoryScheduler(alsList, possibleFactoryAlgorithms);
	}
}

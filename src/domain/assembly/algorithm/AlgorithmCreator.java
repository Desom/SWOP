package domain.assembly.algorithm;

public class AlgorithmCreator {
	//TODO alle algoritme constructors protected?
	
	public BasicSchedulingAlgorithm makeBasic(SchedulingAlgorithm innerAlgorithm){
		return new BasicSchedulingAlgorithm(innerAlgorithm);
	}
	
	public EfficiencySchedulingAlgorithm makeEfficiency(SchedulingAlgorithm innerAlgorithm){
		return new EfficiencySchedulingAlgorithm(innerAlgorithm);
	}
	
	public SpecificationBatchSchedulingAlgorithm makeSpecBatch(SchedulingAlgorithm innerAlgorithm){
		return new SpecificationBatchSchedulingAlgorithm(innerAlgorithm);
	}
	
	public FIFOSchedulingAlgorithm makeFIFO(){
		//TODO deze opslaan zodat we die kunnen flightweigten?
		return new FIFOSchedulingAlgorithm();
	}
	
	public SameOrderSchedulingAlgorithm makeSameOrder(){
		//TODO deze opslaan zodat we die kunnen flightweigten?
		return new SameOrderSchedulingAlgorithm();
	}
}

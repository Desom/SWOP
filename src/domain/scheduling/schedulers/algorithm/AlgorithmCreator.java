package domain.scheduling.schedulers.algorithm;

public class AlgorithmCreator implements AlgorithmCreatorInterface {
	//TODO alle algoritme constructors protected?
	
	@Override
	public BasicSchedulingAlgorithm makeBasic(SchedulingAlgorithm innerAlgorithm){
		return new BasicSchedulingAlgorithm(innerAlgorithm);
	}
	
	@Override
	public EfficiencySchedulingAlgorithm makeEfficiency(SchedulingAlgorithm innerAlgorithm){
		return new EfficiencySchedulingAlgorithm(innerAlgorithm);
	}
	
	@Override
	public SpecificationBatchSchedulingAlgorithm makeSpecBatch(SchedulingAlgorithm innerAlgorithm){
		return new SpecificationBatchSchedulingAlgorithm(innerAlgorithm);
	}
	
	@Override
	public FIFOSchedulingAlgorithm makeFIFO(){
		//TODO deze opslaan zodat we die kunnen flightweigten? JA
		return new FIFOSchedulingAlgorithm();
	}
	
	@Override
	public SameOrderSchedulingAlgorithm makeSameOrder(){
		//TODO deze opslaan zodat we die kunnen flightweigten? JA
		return new SameOrderSchedulingAlgorithm();
	}
}

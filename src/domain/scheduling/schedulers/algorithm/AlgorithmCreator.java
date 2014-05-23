package domain.scheduling.schedulers.algorithm;

public class AlgorithmCreator implements AlgorithmCreatorInterface {
	//TODO alle algoritme constructors protected?
	
	private FIFOSchedulingAlgorithm fifoAlgorithm;
	private SameOrderSchedulingAlgorithm sameOrderAlgorithm;
	
	/**
	 * Constructor of AlgorithmCreator.
	 */
	public AlgorithmCreator() {
		this.fifoAlgorithm = new FIFOSchedulingAlgorithm();
		this.sameOrderAlgorithm = new SameOrderSchedulingAlgorithm();
	}
	
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
		return this.fifoAlgorithm;
	}
	
	@Override
	public SameOrderSchedulingAlgorithm makeSameOrder(){
		return this.sameOrderAlgorithm;
	}
}

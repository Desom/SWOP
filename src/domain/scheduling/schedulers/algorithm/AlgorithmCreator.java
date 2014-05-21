package domain.scheduling.schedulers.algorithm;

public class AlgorithmCreator implements AlgorithmCreatorInterface {
	//TODO alle algoritme constructors protected?
	
	/* (non-Javadoc)
	 * @see domain.assembly.algorithm.AlgorithmCreatorInterface#makeBasic(domain.assembly.algorithm.SchedulingAlgorithm)
	 */
	@Override
	public BasicSchedulingAlgorithm makeBasic(SchedulingAlgorithm innerAlgorithm){
		return new BasicSchedulingAlgorithm(innerAlgorithm);
	}
	
	/* (non-Javadoc)
	 * @see domain.assembly.algorithm.AlgorithmCreatorInterface#makeEfficiency(domain.assembly.algorithm.SchedulingAlgorithm)
	 */
	@Override
	public EfficiencySchedulingAlgorithm makeEfficiency(SchedulingAlgorithm innerAlgorithm){
		return new EfficiencySchedulingAlgorithm(innerAlgorithm);
	}
	
	/* (non-Javadoc)
	 * @see domain.assembly.algorithm.AlgorithmCreatorInterface#makeSpecBatch(domain.assembly.algorithm.SchedulingAlgorithm)
	 */
	@Override
	public SpecificationBatchSchedulingAlgorithm makeSpecBatch(SchedulingAlgorithm innerAlgorithm){
		return new SpecificationBatchSchedulingAlgorithm(innerAlgorithm);
	}
	
	/* (non-Javadoc)
	 * @see domain.assembly.algorithm.AlgorithmCreatorInterface#makeFIFO()
	 */
	@Override
	public FIFOSchedulingAlgorithm makeFIFO(){
		//TODO deze opslaan zodat we die kunnen flightweigten? JA
		return new FIFOSchedulingAlgorithm();
	}
	
	/* (non-Javadoc)
	 * @see domain.assembly.algorithm.AlgorithmCreatorInterface#makeSameOrder()
	 */
	@Override
	public SameOrderSchedulingAlgorithm makeSameOrder(){
		//TODO deze opslaan zodat we die kunnen flightweigten? JA
		return new SameOrderSchedulingAlgorithm();
	}
}

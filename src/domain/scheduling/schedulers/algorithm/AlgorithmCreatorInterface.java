package domain.scheduling.schedulers.algorithm;

public interface AlgorithmCreatorInterface {

	public abstract BasicSchedulingAlgorithm makeBasic(SchedulingAlgorithm innerAlgorithm);

	public abstract EfficiencySchedulingAlgorithm makeEfficiency(SchedulingAlgorithm innerAlgorithm);

	public abstract SpecificationBatchSchedulingAlgorithm makeSpecBatch(SchedulingAlgorithm innerAlgorithm);

	public abstract FIFOSchedulingAlgorithm makeFIFO();

	public abstract SameOrderSchedulingAlgorithm makeSameOrder();

}
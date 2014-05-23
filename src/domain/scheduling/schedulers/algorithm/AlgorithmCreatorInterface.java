package domain.scheduling.schedulers.algorithm;

public interface AlgorithmCreatorInterface {

	/**
	 * Creates a new basic scheduling algorithm with the given inner algorithm.
	 * 
	 * @param innerAlgorithm
	 * 		The inner algorithm of the new basic scheduling algorithm.
	 * @return The new basic scheduling algorithm.
	 */
	public abstract BasicSchedulingAlgorithm makeBasic(SchedulingAlgorithm innerAlgorithm);

	/**
	 * Creates a new efficiency scheduling algorithm with the given algorithm.
	 * 
	 * @param innerAlgorithm
	 * 		The inner algorithm of the new efficiency scheduling algorithm.
	 * @return The new efficiency scheduling algorithm.
	 */
	public abstract EfficiencySchedulingAlgorithm makeEfficiency(SchedulingAlgorithm innerAlgorithm);

	/**
	 * Creates a new specification batch scheduling algorithm with the given algorithm.
	 * 
	 * @param innerAlgorithm
	 * 		The inner algorithm of the new specification batch scheduling algorithm.
	 * @return The new specification batch scheduling algorithm.
	 */
	public abstract SpecificationBatchSchedulingAlgorithm makeSpecBatch(SchedulingAlgorithm innerAlgorithm);

	/**
	 * Creates a new fifo scheduling algorithm with the given algorithm.
	 * 
	 * @param innerAlgorithm
	 * 		The inner algorithm of the new fifo scheduling algorithm.
	 * @return The new fifo scheduling algorithm.
	 */
	public abstract FIFOSchedulingAlgorithm makeFIFO();

	/**
	 * Creates a new same order scheduling algorithm with the given algorithm.
	 * 
	 * @param innerAlgorithm
	 * 		The inner algorithm of the new same order scheduling algorithm.
	 * @return The new same order scheduling algorithm.
	 */
	public abstract SameOrderSchedulingAlgorithm makeSameOrder();

}
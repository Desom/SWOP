package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.configuration.Configuration;
import domain.order.Order;

public class SpecificationBatchSchedulingAlgorithm implements
		SchedulingAlgorithm {

	private Configuration batchConfiguration;
	private SchedulingAlgorithm innerAlgorithm;
	
	/**
	 * Constructor of SpecificationBatchSchedulingAlgorithm.
	 * 
	 * @param innerAlgorithm
	 * 		The inner algorithm of this specification batch algorithm.
	 */
	public SpecificationBatchSchedulingAlgorithm(SchedulingAlgorithm innerAlgorithm) {
		this.batchConfiguration = null;
		this.innerAlgorithm = innerAlgorithm;
	}
	
	/**
	 * Sets the configuration of this specification batch scheduling algorithm.
	 * 
	 * @param configuration
	 * 		The configuration which will have priority.
	 */
	public void setConfiguration(Configuration configuration){
		this.batchConfiguration = configuration;
	}

	/**
	 * Schedules the given list of orders and returns it.
	 * 
	 * @param orderList
	 * 		List of orders to be scheduled.
	 * @param assemblyLineScheduler
	 * 		The scheduler of the assembly line.
	 * @return A scheduled version of the given list of orders.
	 */
	@Override
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList, AssemblyLineScheduler assemblyLineScheduler) {

		ArrayList<Order> batchList = new ArrayList<Order>();
		ArrayList<Order> standardList = new ArrayList<Order>();
		
		for(Order order : orderList){
			if(this.batchConfiguration != null && this.batchConfiguration.equals(order.getConfiguration())){
				batchList.add(order);
			}
			else{
				standardList.add(order);
			}
		}
		
		if(batchList.isEmpty()){
			assemblyLineScheduler.setSchedulingAlgorithmToDefault();
		}

		ArrayList<Order> orderedList = this.innerAlgorithm.scheduleToList(batchList, assemblyLineScheduler);
		
		orderedList.addAll(this.innerAlgorithm.scheduleToList(standardList, assemblyLineScheduler));
		return orderedList;
	}
	
	@Override
	public String toString(){
		return "Specification Batch with " + this.innerAlgorithm.toString();
	}
}

package domain.assembly.algorithm;

import java.util.ArrayList;

import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.configuration.Configuration;
import domain.order.CarOrder;
import domain.order.Order;

public class SpecificationBatchSchedulingAlgorithm implements
		SchedulingAlgorithm {

	private Configuration batchConfiguration;
	private SchedulingAlgorithm innerAlgorithm;
	
	public SpecificationBatchSchedulingAlgorithm(SchedulingAlgorithm innerAlgorithm, Configuration batchConfiguration) {
		this.batchConfiguration = batchConfiguration;
		this.innerAlgorithm = innerAlgorithm;
	}


	@Override
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList,
			AssemblyLineScheduler assemblyLineSchedule) {

		ArrayList<Order> batchList = new ArrayList<Order>();
		ArrayList<Order> standardList = new ArrayList<Order>();
		
		for(Order order : orderList){
			if(this.batchConfiguration.equals(order)){
				batchList.add(order);
			}
			else{
				standardList.add(order);
			}
		}
		

		ArrayList<Order> orderedList = this.innerAlgorithm.scheduleToList(batchList, assemblyLineSchedule);
		
		orderedList.addAll(this.innerAlgorithm.scheduleToList(standardList, assemblyLineSchedule));
		return orderedList;
	}

	@Override
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(
			ArrayList<Order> orderList,
			AssemblyLineScheduler assemblyLineSchedule) {
		// TODO Auto-generated method stub
		return null;
	}

}

package domain.assembly.algorithm;

import java.util.ArrayList;

import domain.assembly.AssemblyLineSchedule;
import domain.configuration.Configuration;
import domain.order.CarOrder;

public class SpecificationBatchSchedulingAlgorithm implements
		SchedulingAlgorithm {

	private Configuration batchConfiguration;
	private SchedulingAlgorithm innerAlgorithm;
	
	public SpecificationBatchSchedulingAlgorithm(SchedulingAlgorithm innerAlgorithm, Configuration batchConfiguration) {
		this.batchConfiguration = batchConfiguration;
		this.innerAlgorithm = innerAlgorithm;
	}

	@Override
	public ArrayList<CarOrder> schedule(
			ArrayList<CarOrder> orderList, 
			AssemblyLineSchedule assemblyLineSchedule) {
		ArrayList<CarOrder> batchList = new ArrayList<CarOrder>();
		ArrayList<CarOrder> standardList = new ArrayList<CarOrder>();
		
		for(CarOrder order : orderList){
			if(this.batchConfiguration.equals(order)){
				batchList.add(order);
			}
			else{
				standardList.add(order);
			}
		}
		

		ArrayList<CarOrder> orderedList = this.innerAlgorithm.schedule(batchList, assemblyLineSchedule);
		
		orderedList.addAll(this.innerAlgorithm.schedule(standardList, assemblyLineSchedule));
		return orderedList;
	}

}

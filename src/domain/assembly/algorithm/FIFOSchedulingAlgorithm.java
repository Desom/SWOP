package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import domain.assembly.AssemblyLineSchedule;
import domain.order.CarOrder;

public class FIFOSchedulingAlgorithm implements SchedulingAlgorithm {
	
	private Comparator<CarOrder> comparator;
	
	public FIFOSchedulingAlgorithm() {
		comparator = new Comparator<CarOrder>(){
			@Override
			public int compare(CarOrder order1, CarOrder order2){
				return order1.getOrderedTime().compareTo(order2.getOrderedTime());
			}
		};
	}

	@Override
	public ArrayList<CarOrder> schedule(
			ArrayList<CarOrder> orderList, 
			AssemblyLineSchedule assemblyLineSchedule) {
		
		//TODO hier mss de gekregen list eerst copieren?
		Collections.sort(orderList, this.comparator);
		return orderList;
	}

}

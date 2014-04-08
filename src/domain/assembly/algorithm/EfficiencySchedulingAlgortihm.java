package domain.assembly.algorithm;

import java.util.ArrayList;

import domain.assembly.AssemblyLineSchedule;
import domain.order.CarOrder;

public class EfficiencySchedulingAlgortihm implements SchedulingAlgorithm {

	private SchedulingAlgorithm innerAlgorithm;
	
	public EfficiencySchedulingAlgortihm(SchedulingAlgorithm innerAlgorithm) {
		this.innerAlgorithm = innerAlgorithm;
	}

	@Override
	public ArrayList<CarOrder> schedule(
			ArrayList<CarOrder> orderList, 
			AssemblyLineSchedule assemblyLineSchedule) {
		// TODO Auto-generated method stub
		return null;
	}

}

package domain.assembly.algorithm;

import java.util.ArrayList;

import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.order.CarOrder;
import domain.order.Order;

public class EfficiencySchedulingAlgorithm implements SchedulingAlgorithm {

	private SchedulingAlgorithm innerAlgorithm;
	
	public EfficiencySchedulingAlgorithm(SchedulingAlgorithm innerAlgorithm) {
		this.innerAlgorithm = innerAlgorithm;
	}

	@Override
	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList,
			AssemblyLineScheduler assemblyLineSchedule) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(
			ArrayList<Order> orderList,
			AssemblyLineScheduler assemblyLineSchedule) {
		// TODO Auto-generated method stub
		return null;
	}



}

package domain.assembly.algorithm;

import java.util.ArrayList;

import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.order.CarOrder;
import domain.order.Order;

public interface SchedulingAlgorithm {

	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList, AssemblyLineScheduler assemblyLineSchedule);
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(ArrayList<Order> orderList, AssemblyLineScheduler assemblyLineSchedule);
}

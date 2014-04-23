package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.order.Order;

public interface SchedulingAlgorithm {

	public ArrayList<Order> scheduleToList(ArrayList<Order> orderList, AssemblyLineScheduler assemblyLineScheduler);
	public ArrayList<ScheduledOrder> scheduleToScheduledOrderList(ArrayList<Order> orderList, GregorianCalendar allTasksCompletedTime, AssemblyLineScheduler assemblyLineScheduler);
}

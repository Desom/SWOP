package domain.scheduling.schedulers.algorithm;

import java.util.ArrayList;
import java.util.HashMap;

import domain.scheduling.order.Order;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.FactoryScheduler;

public interface FactorySchedulingAlgorithm {

	/**
	 * Assigns all the given orders to one of the AssemblyLineSchedulers of factoryScheduler.
	 * 
	 * @param orders
	 * 		The orders which will be assigned to an AssemblyLineScheduler.
	 * @param factoryScheduler
	 * 		The FactoryScheduler who wants to assign these orders to the 
	 * @return
	 * 		A mapping of AssemblyLineSchedulers and their assigned orders.
	 */
	public HashMap<AssemblyLineScheduler, ArrayList<Order>> assignOrders(ArrayList<Order> orders,
			FactoryScheduler factoryScheduler);

}

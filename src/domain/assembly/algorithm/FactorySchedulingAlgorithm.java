package domain.assembly.algorithm;

import java.util.ArrayList;

import domain.assembly.FactoryScheduler;
import domain.order.Order;

public interface FactorySchedulingAlgorithm {

	public ArrayList<ArrayList<Order>> allocateOrders(ArrayList<Order> ordersFor,
			FactoryScheduler factoryScheduler);

}

package domain.assembly.algorithm;

import java.util.ArrayList;
import java.util.LinkedList;

import domain.Statistics;
import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.order.Order;

public class dummyAssemblyLine extends AssemblyLine {
	LinkedList<Order> orders;
	public dummyAssemblyLine(AssemblyLineScheduler assemblyLineScheduler,
			Statistics statistics) {
		super(assemblyLineScheduler, statistics);
		orders= new LinkedList<Order>();
		orders.add(null);
		orders.add(null);
		orders.add(null);
	}
	public void add(Order order){
		orders.removeLast();
		orders.addFirst(order);
	}
	
	@Override
	public LinkedList<Order> getAllOrders() {
		return (LinkedList<Order>) orders.clone();
	}
}

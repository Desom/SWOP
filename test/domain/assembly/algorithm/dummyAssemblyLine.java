package domain.assembly.algorithm;

import java.util.LinkedList;

import domain.Statistics;
import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.CannotAdvanceException;
import domain.order.Order;

public class dummyAssemblyLine extends AssemblyLine {
	LinkedList<Order> orders;
	public dummyAssemblyLine(AssemblyLineScheduler assemblyLineScheduler,
			Statistics statistics) {
		super(assemblyLineScheduler);
		orders= new LinkedList<Order>();
		orders.add(null);
		orders.add(null);
		orders.add(null);
	}
	public void add(Order order){
		orders.removeLast();
		orders.addFirst(order);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public LinkedList<Order> getAllOrders() {
		return (LinkedList<Order>) orders.clone();
	}
	@Override
	public void advanceLine() throws CannotAdvanceException {
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public LinkedList<Order> StateWhenAcceptingOrders() {
		LinkedList<Order> temp = (LinkedList<Order>) this.orders.clone();
		return temp;
	}
}

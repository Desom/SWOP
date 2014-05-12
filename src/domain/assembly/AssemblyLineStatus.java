package domain.assembly;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.order.Order;

public interface AssemblyLineStatus {

	public void advanceLine(AssemblyLine assemblyLine) throws CannotAdvanceException;
	public Boolean canAdvanceLine(AssemblyLine assemblyLine);
	public Boolean canAcceptNewOrders();
	public LinkedList<Order> stateWhenAcceptingOrders(AssemblyLine assemblyLine);
	public GregorianCalendar timeWhenAcceptingOrders(AssemblyLine assemblyLine);
}

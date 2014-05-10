package domain.assembly;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.order.Order;

public class BrokenStatus implements AssemblyLineStatus {

	@Override
	public void advanceLine(AssemblyLine assemblyLine) {
		return;
	}

	@Override
	public Boolean canAdvanceLine(AssemblyLine assemblyLine) {
		return false;
	}

	@Override
	public Boolean canAcceptNewOrders() {
		return false;

	}
	@Override
	public LinkedList<Order> StateWhenAcceptingOrders(AssemblyLine assemblyLine) {
		return null;
	}

	@Override
	public GregorianCalendar TimeWhenAcceptingOrders(AssemblyLine assemblyLine) {
		return null;
	}

}

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
	public LinkedList<Order> stateWhenAcceptingOrders(AssemblyLine assemblyLine) {
		return null;
	}

	@Override
	public GregorianCalendar timeWhenAcceptingOrders(AssemblyLine assemblyLine) {
		return null;
	}
	
	@Override
	public String toString() {
		return "Broken";
	}

}

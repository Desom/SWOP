package domain.assembly;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import domain.order.Order;

public class OperationalStatus extends Status {

	@Override
	public void advanceLine(AssemblyLine assemblyLine) throws CannotAdvanceException {
		this.standardAdvanceLine(assemblyLine);
	}

	@Override
	public Boolean canAdvanceLine(AssemblyLine assemblyLine) {
		for(Workstation workstation : assemblyLine.getAllWorkstations())
			if(!workstation.hasAllTasksCompleted())
				return false;
		return true;
	}

	@Override
	public Boolean canAcceptNewOrders() {
		return true;
	}

	@Override
	public LinkedList<Order> stateWhenAcceptingOrders(AssemblyLine assemblyLine) {
		LinkedList<Order> result = assemblyLine.getAllOrders();
		return result;
	}

	@Override
	public GregorianCalendar timeWhenAcceptingOrders(AssemblyLine assemblyLine) {
		GregorianCalendar result =(GregorianCalendar) assemblyLine.getAssemblyLineScheduler().getCurrentTime().clone();
		if(!this.canAdvanceLine(assemblyLine))
			result.add(GregorianCalendar.MINUTE, assemblyLine.calculateTimeTillAdvanceFor(assemblyLine.getAllOrders()));
		return result;
	}

	@Override
	public String toString() {
		return "Operational";
	}

	@Override
	protected Order notifyOrderAsked(AssemblyLine assemblyLine) {
		try {
			return assemblyLine.getAssemblyLineScheduler().getNextOrder(0);
		} catch (NoOrdersToBeScheduledException e) {
			return null;
		}
	}

}

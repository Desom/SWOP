package domain.assembly.assemblyline.status;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.assemblyline.AssemblyLine;
import domain.scheduling.order.Order;

public class BrokenStatus extends AssemblyLineStatus {
	
	/**
	 * Constructor of BrokenStatus.
	 * 
	 * @param creator
	 * 		The creator of statuses.
	 */
	public BrokenStatus(StatusCreatorInterface creator) {
		super(creator);
	}

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

	@Override
	public int calculateTimeTillEmptyFor(AssemblyLine assemblyLine,
			LinkedList<Order> assembly) {
		return -1;
	}

}

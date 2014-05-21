package domain.assembly.assemblyline.status;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.CannotAdvanceException;
import domain.scheduling.NoOrdersToBeScheduledException;
import domain.scheduling.order.Order;

public class OperationalStatus extends AbstractAdvancingStatus {

	public OperationalStatus(StatusCreatorInterface creator) {
		super(creator);
	}

	@Override
	public void advanceLine(AssemblyLine assemblyLine) throws CannotAdvanceException {
		this.standardAdvanceLine(assemblyLine);
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
	protected Order notifyOrderAsked(AssemblyLine assemblyLine) {
		try {
			return assemblyLine.getAssemblyLineScheduler().getNextOrder(0);
		} catch (NoOrdersToBeScheduledException e) {
			return null;
		}
	}

}

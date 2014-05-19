package domain.assembly;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.order.Order;

public class OperationalStatus extends Status {

	@Override
	public void advanceLine(AssemblyLine assemblyLine) throws CannotAdvanceException {
		// Check of alle tasks klaar zijn, zoniet laat aan de user weten welke nog niet klaar zijn (zie exception message).
		if (!this.canAdvanceLine(assemblyLine))
			throw new CannotAdvanceException(assemblyLine.getBlockingWorkstations());


		// Zoek de tijd die nodig was om alle tasks uit te voeren.
		int timeSpendForTasks = 0;
		for(Workstation workstation : assemblyLine.getAllWorkstations()){
			if(workstation.getTimeSpend() > timeSpendForTasks)
				timeSpendForTasks = workstation.getTimeSpend();
		}
		// Vraag nieuwe order op.
		boolean tryNextAdvance = true;
		Order newOrder = null;
		try {
			newOrder = assemblyLine.getAssemblyLineScheduler().getNextOrder(timeSpendForTasks);
		} catch (NoOrdersToBeScheduledException e) {
			tryNextAdvance = false;
		}
		
		this.advanceLine(assemblyLine, newOrder, tryNextAdvance);
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

}

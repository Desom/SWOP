package domain.assembly;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.order.Order;

public class MaintenanceStatus extends Status{

	@Override
	public void advanceLine(AssemblyLine assemblyLine) throws CannotAdvanceException {
		this.standardAdvanceLine(assemblyLine);
		if(assemblyLine.isEmpty()){
			assemblyLine.getAssemblyLineScheduler().addCurrentTime(240);
			assemblyLine.setCurrentStatus(new OperationalStatus());
		}
	}

	@Override
	public Boolean canAdvanceLine(AssemblyLine assemblyLine) {
		// TODO Auto-generated method stub
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
		LinkedList<Order> result = new LinkedList<Order>();
		for(int i =0; i<assemblyLine.getNumberOfWorkstations();i++){
			result.add(null);
		}
		return result;
	}

	@Override
	public GregorianCalendar timeWhenAcceptingOrders(AssemblyLine assemblyLine) {
		GregorianCalendar result =(GregorianCalendar) assemblyLine.getAssemblyLineScheduler().getCurrentTime().clone();
		if(!assemblyLine.isEmpty())
		result.add(GregorianCalendar.MINUTE, 240+assemblyLine.calculateTimeTillEmptyFor(assemblyLine.getAllOrders()));
		return result;
	}
	
	@Override
	public String toString() {
		return "Maintenance";
	}

	@Override
	protected Order notifyOrderAsked(AssemblyLine assemblyLine) {
		return null;
	}

}

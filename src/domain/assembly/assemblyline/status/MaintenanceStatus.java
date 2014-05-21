package domain.assembly.assemblyline.status;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.CannotAdvanceException;
import domain.scheduling.order.Order;

public class MaintenanceStatus extends AbstractAdvancingStatus{

	/**
	 * Constructor of MaintenanceStatus.
	 * 
	 * @param creator
	 * 		The assembly line status creator used to get other statuses.
	 */
	public MaintenanceStatus(StatusCreatorInterface creator) {
		super(creator);
	}

	/**
	 * Advances the assembly line until without adding orders. When it is empty, the assembly line won't be operational for 4 hours.
	 * After those 4 hours, it will be operational again.
	 */
	@Override
	public void advanceLine(AssemblyLine assemblyLine) throws CannotAdvanceException {
		this.standardAdvanceLine(assemblyLine);
		if(assemblyLine.isEmpty()){
			assemblyLine.getAssemblyLineScheduler().addCurrentTime(240);
			assemblyLine.setCurrentStatus(this.creator.getOperationalStatus());
		}
	}

	@Override
	public LinkedList<Order> stateWhenAcceptingOrders(AssemblyLine assemblyLine) {
		LinkedList<Order> result = new LinkedList<Order>();
		for(int i = 0; i < assemblyLine.getNumberOfWorkstations(); i++){
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

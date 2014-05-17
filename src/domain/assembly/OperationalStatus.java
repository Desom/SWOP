package domain.assembly;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.InternalFailureException;
import domain.order.Order;

public class OperationalStatus implements AssemblyLineStatus {

	@Override
	public void advanceLine(AssemblyLine assemblyLine)
			throws CannotAdvanceException {
		// check of alle tasks klaar zijn, zoniet laat aan de user weten welke nog niet klaar zijn (zie exception message).
				if (!this.canAdvanceLine(assemblyLine))
					throw new CannotAdvanceException(assemblyLine.getBlockingWorkstations());

				try{
					//zoek de tijd die nodig was om alle tasks uit te voeren.
					int timeSpendForTasks = 0;
					for(Workstation workstation : assemblyLine.getAllWorkstations()){
						if(workstation.getTimeSpend() > timeSpendForTasks)
							timeSpendForTasks = workstation.getTimeSpend();
					}

					// move huidige vehicles 1 plek
					//neem vehicle van WorkStation 3
					Workstation workstationLast = assemblyLine.selectWorkstationById(assemblyLine.getNumberOfWorkstations());
					Order finished = null;
					if(workstationLast.getVehicleAssemblyProcess() != null){
						// zoek welke order klaar is, wacht met het zetten van de deliveryTime omdat de tijd van het schedule nog moet worden geupdate.
						finished = workstationLast.getVehicleAssemblyProcess().getOrder();
					}

					//voeg nieuwe vehicle toe.
					boolean tryNextAdvance = true;
					Order newOrder = null;
					try {
						newOrder = assemblyLine.getAssemblyLineScheduler().getNextOrder(timeSpendForTasks);
					} catch (NoOrdersToBeScheduledException e) {
						tryNextAdvance = false;
					}


					for(int i = assemblyLine.getAllWorkstations().size(); i>1; i--){
						Workstation workstationNext = assemblyLine.selectWorkstationById(i);
						workstationNext.clear();;
						Workstation workstationPrev = assemblyLine.selectWorkstationById(i-1);
						workstationNext.setVehicleAssemblyProcess(workstationPrev.getVehicleAssemblyProcess());
					}



					VehicleAssemblyProcess newAssemblyProcess = null;
					if(newOrder != null){
						newAssemblyProcess = newOrder.getAssemblyprocess();
					}

					Workstation workstation1 = assemblyLine.selectWorkstationById(1);
					workstation1.clear();
					workstation1.setVehicleAssemblyProcess(newAssemblyProcess);

					if(finished != null){
						finished.getAssemblyprocess().setDeliveredTime(assemblyLine.getAssemblyLineScheduler().getCurrentTime());
						finished.getAssemblyprocess().registerDelay(assemblyLine);
					}

					if(tryNextAdvance && assemblyLine.canAdvanceLine()){
						try{
							assemblyLine.advanceLine();
						}
						catch(CannotAdvanceException e){
							throw new InternalFailureException("The AssemblyLine couldn't advance even though canAdvanceLine() returned true.");
						}
					}
				}
				catch(DoesNotExistException e){
					throw new InternalFailureException("Suddenly a Workstation disappeared while that should not be possible.");
				}

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

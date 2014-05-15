package domain.assembly;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.InternalFailureException;
import domain.order.Order;

public class MaintenanceStatus implements AssemblyLineStatus{

	@Override
	public void advanceLine(AssemblyLine assemblyLine) throws CannotAdvanceException {
		if (!this.canAdvanceLine(assemblyLine))
			throw new CannotAdvanceException(assemblyLine.getBlockingWorkstations());

		try{
			//zoek de tijd die nodig was om alle tasks uit te voeren.
			int timeSpendForTasks = 0;
			for(Workstation workstation : assemblyLine.getAllWorkstations()){
				if(workstation.getTimeSpend() > timeSpendForTasks)
					timeSpendForTasks = workstation.getTimeSpend();
			}

			// move huidige cars 1 plek
			//neem CarOrder van WorkStation 3
			Workstation workstationLast = assemblyLine.selectWorkstationById(assemblyLine.getNumberOfWorkstations());
			Order finished = null;
			if(workstationLast.getCarAssemblyProcess() != null){
				// zoek welke CarOrder klaar is, wacht met het zetten van de deliveryTime omdat de tijd van het schedule nog moet worden geupdate.
				finished = workstationLast.getCarAssemblyProcess().getOrder();
			}




			for(int i = assemblyLine.getAllWorkstations().size(); i>1; i--){
				Workstation workstationNext = assemblyLine.selectWorkstationById(i);
				workstationNext.clear();;
				Workstation workstationPrev = assemblyLine.selectWorkstationById(i-1);
				workstationNext.setCarAssemblyProcess(workstationPrev.getCarAssemblyProcess());
			}



			CarAssemblyProcess newAssemblyProcess = null;


			Workstation workstation1 = assemblyLine.selectWorkstationById(1);
			workstation1.clear();
			workstation1.setCarAssemblyProcess(newAssemblyProcess);

			if(finished != null){
				finished.getAssemblyprocess().setDeliveredTime(assemblyLine.getAssemblyLineScheduler().getCurrentTime());
				finished.getAssemblyprocess().registerDelay(assemblyLine);
			}
			if(!assemblyLine.isEmpty()) return;
			assemblyLine.setCurrentStatus(new OperationalStatus());
			assemblyLine.getAssemblyLineScheduler().addCurrentTime(240);
			try{
			assemblyLine.advanceLine();
			}
			catch(CannotAdvanceException e){
				
			}
		}
		catch(DoesNotExistException e){
			throw new InternalFailureException("Suddenly a Workstation disappeared while that should not be possible.");
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

}

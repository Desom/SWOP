package domain.assembly.assemblyline.status;

import java.util.ArrayList;
import java.util.LinkedList;

import domain.InternalFailureException;
import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.CannotAdvanceException;
import domain.assembly.assemblyline.DoesNotExistException;
import domain.assembly.workstations.VehicleAssemblyProcess;
import domain.assembly.workstations.Workstation;
import domain.scheduling.order.Order;

public abstract class AbstractAdvancingStatus extends AssemblyLineStatus {
	
	/**
	 * Constructor of AbstractAdvancingStatus.
	 * 
	 * @param creator
	 * 		The creator of statuses.
	 */
	public AbstractAdvancingStatus(StatusCreatorInterface creator) {
		super(creator);
	}

	/**
	 * Advances the assembly line.
	 * 
	 * @param assemblyLine
	 * 		The assembly line to be advanced.
	 * @throws CannotAdvanceException
	 * 		If the assembly line cannot advance.
	 * @throws IllegalStateException
	 * 		If the assembly line can't complete the next order to be placed on the assembly line.
	 * @throws InternalFailureException
	 * 		If a workstation of the assembly line disappeared.
	 */
	protected void standardAdvanceLine(AssemblyLine assemblyLine) throws CannotAdvanceException, IllegalStateException, InternalFailureException {
		// Check of alle tasks klaar zijn, zoniet laat aan de user weten welke nog niet klaar zijn (zie exception message).
		if (!this.canAdvanceLine(assemblyLine))
			throw new CannotAdvanceException(assemblyLine.getBlockingWorkstations());


		// Zoek de tijd die nodig was om alle tasks uit te voeren.
		int timeSpendForTasks = 0;
		for(Workstation workstation : assemblyLine.getAllWorkstations()){
			if(workstation.getTimeSpent() > timeSpendForTasks)
				timeSpendForTasks = workstation.getTimeSpent();
		}
		assemblyLine.getAssemblyLineScheduler().addCurrentTime(timeSpendForTasks);
		// Vraag nieuwe order op.
		Order newOrder = notifyOrderAsked(assemblyLine);
		
		// Controleer of deze order wel op deze assembly line voltooid kan worden
		if (!assemblyLine.canDoOrder(newOrder)) // TODO IllegalStateException goed?
			throw new IllegalStateException("The assembly line can't complete the order received from its scheduler.");
		
		try{		

			// move huidige vehicles 1 plek
			//neem vehicle van WorkStation 3
			Workstation workstationLast = assemblyLine.selectWorkstationById(assemblyLine.getNumberOfWorkstations());
			ArrayList<Order> finishedOrders = new ArrayList<Order>();
			if(workstationLast.getVehicleAssemblyProcess() != null){
				// zoek welke order klaar is, wacht met het zetten van de deliveryTime omdat de tijd van het schedule nog moet worden geupdate.
				finishedOrders.add(workstationLast.getVehicleAssemblyProcess().getOrder());
			}


			for(int i = assemblyLine.getAllWorkstations().size(); i>1; i--){
				Workstation workstationNext = assemblyLine.selectWorkstationById(i);
				workstationNext.clear();
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

			// Er wordt gecheckt of er workstations geskipt kunnen worden.
			for (int i = assemblyLine.getAllWorkstations().size(); i > 0; i--) {
				for (int id = i; id <= assemblyLine.getAllWorkstations().size(); id++) {
					// als de eerste plaats van de workstation leeg is plaats nieuw order
					if(id ==1 && assemblyLine.getAllOrders().get(0) ==null){
						Order nextOrder = this.notifyOrderAskedSkip(assemblyLine);
						if(nextOrder != null){
							VehicleAssemblyProcess nextAssemblyProcess = newOrder.getAssemblyprocess();
							workstation1.setVehicleAssemblyProcess(nextAssemblyProcess);
							i++;
						}
					}
					// Er wordt gecheckt welke workstation geen taken uit te voeren heeft.
					if (assemblyLine.selectWorkstationById(id).getAllPendingTasks().isEmpty() && assemblyLine.selectWorkstationById(id).getVehicleAssemblyProcess() != null) {					

						// Als dit niet de laatste workstation is en de volgende is vrij, dan wordt het proces verschoven naar de volgende.
						if (id < assemblyLine.getAllWorkstations().size() && assemblyLine.selectWorkstationById(id + 1).getVehicleAssemblyProcess() == null) {
							assemblyLine.selectWorkstationById(id + 1).setVehicleAssemblyProcess(assemblyLine.selectWorkstationById(id).getVehicleAssemblyProcess());
							assemblyLine.selectWorkstationById(id).clear();
						}
						// Als dit de laatste workstation is, wordt het assembly process van de band gehaald.
						else if (assemblyLine.selectWorkstationById(id).getVehicleAssemblyProcess() != null) {
							finishedOrders.add(assemblyLine.selectWorkstationById(id).getVehicleAssemblyProcess().getOrder());
						}
					}
				}
			}

			for (Order finishedOrder : finishedOrders){
				finishedOrder.getAssemblyprocess().finish(assemblyLine);;
			}
		}
		catch(DoesNotExistException e){
			throw new InternalFailureException("Suddenly a Workstation disappeared while that should not be possible.");
		}
	}

	/**
	 * Notifies that there is room for a new order on the assembly line due to an advance line operation.
	 * 
	 * @param assemblyLine
	 * 		The assemblyLine which has room for a new order.
	 * @return The new order.
	 */
	protected abstract Order notifyOrderAsked(AssemblyLine assemblyLine);
	
	/**
	 * Notifies that there is room for a new order on the assembly line due to a skip workstation operation.
	 * 
	 * @param assemblyLine
	 * 		The assemblyLine which has room for a new order.
	 * @return The new order.
	 */
	protected abstract Order notifyOrderAskedSkip(AssemblyLine assemblyLine);
	
	@Override
	public abstract void advanceLine(AssemblyLine assemblyLine) throws CannotAdvanceException;
	
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

	public int calculateTimeTillEmptyFor(AssemblyLine assemblyLine, LinkedList<Order> assembly) {
		@SuppressWarnings("unchecked")
		LinkedList<Order> simulAssembly = (LinkedList<Order>) assembly.clone();
		int time = 0;
		for(int i = 0; i < assemblyLine.getNumberOfWorkstations(); i++){
			time += assemblyLine.calculateTimeTillAdvanceFor(simulAssembly);
			simulAssembly.removeLast();
			simulAssembly.addFirst(null);
			for(int j= assemblyLine.getNumberOfWorkstations()-1; j>=0;j--){
				try {
					if(simulAssembly.get(j) != null && !assemblyLine.filterWorkstations(simulAssembly.get(j).getAssemblyprocess()).contains(assemblyLine.selectWorkstationById(j))){
						if(j==assemblyLine.getNumberOfWorkstations()-1){
							simulAssembly.removeLast();
							simulAssembly.addLast(null);
						}
						else{
							if(simulAssembly.get(j+1)==null){
								simulAssembly.remove(j+1);
								simulAssembly.add(j+1, simulAssembly.get(j));
								simulAssembly.remove(j);
								simulAssembly.add(j, null);
								j+=2;
							}
						}
					}
				} catch (DoesNotExistException e) {
					// onmogelijk
				}
			}
		}
		return time;
	}
}

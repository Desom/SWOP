package domain.assembly.assemblyline;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import domain.InternalFailureException;
import domain.assembly.assemblyline.status.AssemblyLineStatus;
import domain.assembly.assemblyline.status.AssemblyStatusView;
import domain.assembly.workstations.VehicleAssemblyProcess;
import domain.assembly.workstations.Workstation;
import domain.assembly.workstations.WorkstationObserver;
import domain.configuration.models.VehicleModel;
import domain.scheduling.order.Order;
import domain.scheduling.schedulers.AssemblyLineScheduler;

public class AssemblyLine implements WorkstationObserver{

	private ArrayList<Workstation> workstations = new ArrayList<Workstation>();
	private final AssemblyLineScheduler assemblyLineScheduler;
	private AssemblyLineStatus currentStatus;
	private ArrayList<VehicleModel> possibleModels;

	/**
	 * Constructor of AssemblyLine.
	 * 
	 * @param assemblyLineScheduler
	 * 		The scheduler of this assembly line.
	 * @param possibleStatuses
	 * 		The possible statuses of this assemblyLine.
	 * 		The first status in this lists will be the current status upon creation of this assembly line.
	 * @param possibleModels
	 * 		Only these VehicleModels will be able to be built on this assembly line.
	 */
	public AssemblyLine(AssemblyLineScheduler assemblyLineScheduler, AssemblyLineStatus currentStatus, ArrayList<VehicleModel> possibleModels){
		this.assemblyLineScheduler = assemblyLineScheduler;
		this.assemblyLineScheduler.setAssemblyLine(this);
		this.possibleModels = new ArrayList<VehicleModel>(possibleModels);
		this.currentStatus = currentStatus;
		try {
			this.advanceLine();
		} catch (CannotAdvanceException e) {
			//geen probleem dan moet de assemblyLine maar wachten.
		}
	}

	/**
	 * 
	 * @return A linked list containing all the workStations.
	 */
	public LinkedList<Workstation> getAllWorkstations(){
		return new LinkedList<Workstation>(workstations);
	}

	public AssemblyLineScheduler getAssemblyLineScheduler() {
		return assemblyLineScheduler;
	}

	/**
	 * Returns whether this assembly line can advance or not.
	 * 
	 * @return true if this assembly line can advance, otherwise false.
	 */
	public boolean canAdvanceLine() {
		return this.currentStatus.canAdvanceLine(this);
	}

	public ArrayList<Workstation> getBlockingWorkstations() {
		ArrayList<Workstation> blockingWorkstations = new ArrayList<Workstation>();
		for (Workstation workstation : this.getAllWorkstations())
			if (!workstation.hasAllTasksCompleted())
				blockingWorkstations.add(workstation);
		return blockingWorkstations;
	}

	/**
	 * Advances the assembly line.
	 * 
	 * @throws CannotAdvanceException 
	 * 		If there are workstations that are blocking the assembly line.
	 */
	public void advanceLine() throws CannotAdvanceException {
		this.currentStatus.advanceLine(this);
	}

	/**
	 * Selects the workstation with the specified id from the list of all workstations
	 * 
	 * @param id
	 * 		The ID of the desired workstation.
	 * @return The workstation that matches the specified ID.
	 * @throws DoesNotExistException
	 * 		If no workstation with the specified ID exists.
	 */
	public Workstation selectWorkstationById(int id) throws DoesNotExistException{
		if(workstations.size() > (id - 1))
			return workstations.get(id - 1);
		throw new DoesNotExistException("No workstation exists with ID: " + id);
	}

	
	// OUTDATED TODO
	/*
	 * This method creates 3 workstations, specifies their ID's and the respective assembly task types those workstations can perform.
	 * 
	 * @return A list of the created workstations.
	private ArrayList<Workstation> createWorkstations(){
		ArrayList<Workstation> workstations = new ArrayList<Workstation>();
		ArrayList<OptionType> taskTypes1 = new ArrayList<OptionType>();
		taskTypes1.add(OptionType.Body);
		taskTypes1.add(OptionType.Color);
		Workstation workStation1 = new Workstation(this, "W1", taskTypes1);

		ArrayList<OptionType> taskTypes2 = new ArrayList<OptionType>();
		taskTypes2.add(OptionType.Engine);
		taskTypes2.add(OptionType.Gearbox);
		Workstation workStation2 = new Workstation(this, "W2", taskTypes2);

		ArrayList<OptionType> taskTypes3 = new ArrayList<OptionType>();
		taskTypes3.add(OptionType.Seats);
		taskTypes3.add(OptionType.Airco);
		taskTypes3.add(OptionType.Wheels);
		taskTypes3.add(OptionType.Spoiler);
		Workstation workStation3 = new Workstation(this, "W3", taskTypes3);

		workstations.add(workStation1);
		workstations.add(workStation2);
		workstations.add(workStation3);
		return workstations;
	}*/

	/**
	 * Creates a view of the current status of the assembly line.
	 * 
	 * @return An AssemblyStatusView representing the current status.
	 */
	public AssemblyStatusView currentStatus(){
		AssemblyStatusView view = new AssemblyStatusView(workstations, "Current Status");
		return view;
	}

	/**
	 * Returns all workstation id's on this assembly line.
	 * 
	 * @return All workstation id's.
	 */
	// TODO
	//	public LinkedList<Integer> getWorkstationIDs(){
	//		LinkedList<Integer> ids= new LinkedList<Integer>();
	//		for(Workstation w: workstations){
	//			ids.add(w.getId());
	//		}
	//		return ids;
	//	}

	/**
	 * Returns the number of workstation on this assembly line.
	 * 
	 * @return The number of workstations on the assembly line
	 */
	public int getNumberOfWorkstations(){
		return workstations.size();
	}

	/**
	 * Returns a list of the Orders the Workstations are working on.
	 * 
	 * @return A list of all orders currently in the respective Workstation as
	 *         indicated by the position in the list. (null if Workstation is
	 *         empty).
	 */
	public LinkedList<Order> getAllOrders() {
		LinkedList<Order> orders = new LinkedList<Order>();
		for(Workstation w : getAllWorkstations()){
			if(w.getVehicleAssemblyProcess() != null){
				orders.addLast(w.getVehicleAssemblyProcess().getOrder());
			}
			else{
				orders.addLast(null);
			}
		}
		return orders;
	}

	/**
	 * Calculates the estimated time necessary to empty the assemblyLine, based on the given orders.
	 * 
	 * @param assembly
	 * 		A list of Orders which represents the assemblyLine. ( null if there is no order on that respective Workstation.)
	 * @return The calculated amount of minutes it will take to empty the given assemblyLine.
	 */
	public int calculateTimeTillEmptyFor(LinkedList<Order> assembly) {
		return this.currentStatus.calculateTimeTillEmptyFor(this,assembly);
	}

	/**
	 * Calculates the estimated amount of minutes it will take to complete all
	 * tasks on all workstations if the given Orders are on the assemblyLine.
	 * 
	 * @param assembly
	 * 		The Orders that are on the assemblyLine.
	 * @return The amount of minutes it will take to complete all tasks on all workstations with the given assembly line occupation.
	 */
	public int calculateTimeTillAdvanceFor(LinkedList<Order> assembly) {
		LinkedList<Workstation> allWorkstations = this.getAllWorkstations();
		int maxTime = 0;
		for(int j = 0; j < this.getNumberOfWorkstations(); j++){
			if(assembly.get(j) != null
					&& (this.filterWorkstations(assembly.get(j).getAssemblyprocess())).contains(allWorkstations.get(j))
					&& assembly.get(j).getConfiguration().getExpectedWorkingTime(allWorkstations.get(j).getWorkstationType()) > maxTime){
				maxTime = assembly.get(j).getConfiguration().getExpectedWorkingTime(allWorkstations.get(j).getWorkstationType());
			}
		}
		return maxTime;
	}

	/**
	 * Returns whether the assembly line is empty or not.
	 * 
	 * @return True if the assembly line is empty, otherwise false.
	 */
	public boolean isEmpty() {
		for(Workstation workstation : getAllWorkstations()){
			if(workstation.getVehicleAssemblyProcess() != null){
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns all workstations which can complete tasks of the given vehicle assembly process.
	 * 
	 * @param process
	 * 		The vehicle assembly process to be checked.
	 * @return All workstations required to complete all the tasks of this vehicle assembly process.
	 */
	public ArrayList<Workstation> filterWorkstations(VehicleAssemblyProcess process){
		ArrayList<Workstation> filteredWorkstations = new ArrayList<Workstation>();
		for(Workstation workstation : workstations){
			if(workstation.compatibleWith(process).size() > 0){
				filteredWorkstations.add(workstation);
			}
		}
		return filteredWorkstations;
	}

	/**
	 * Sets the current status of this assembly line.
	 * 
	 * @param newStatus
	 * 		The new current status.
	 */
	public void setCurrentStatus(AssemblyLineStatus newStatus) throws IllegalArgumentException {
		this.currentStatus = newStatus;
	}
	
	public AssemblyLineStatus getCurrentStatus() {
		return this.currentStatus;
	}
	
	public Boolean canAcceptNewOrders() {
		return this.currentStatus.canAcceptNewOrders();
	}
	
	public LinkedList<Order> stateWhenAcceptingOrders() {
		return this.currentStatus.stateWhenAcceptingOrders(this);
	}

	public GregorianCalendar timeWhenAcceptingOrders() {
		return this.currentStatus.timeWhenAcceptingOrders(this);
	}
	
	/**
	 * Adds the given workstation to the end of the assemblyLine.
	 * If the workstation does not yet have an assemblyLine.
	 * 
	 * @param workstation the specified workstation
	 */
	protected void addWorkstation(Workstation workstation){
		workstations.add(workstation);
		workstation.addObserver(this);
	}
	
	/**
	 * Add all the given workstations to the end of the assemblyLine.
	 * If the workstations do not yet have an assemblyLine.
	 * 
	 * @param list a list of the specified workstations
	 */
	protected void addAllWorkstation(List<Workstation> list){
		for(Workstation w:list){
			addWorkstation(w);
		}
	}

	/**
	 * React to a change in a workstation.
	 */
	@Override
	public void update() {
		if(this.canAdvanceLine()){
			try {
				this.advanceLine();
			} catch (CannotAdvanceException e) {
				throw new InternalFailureException("The AssemblyLine couldn't advance even though canAdvanceLine() returned true.");
				}
		}
	}

	/**
	 * Returns all the models whose orders can be completed on this assemblyLine.
	 * 
	 * @return A list of the models whose orders can be completed on this assemblyLine.
	 */
	public ArrayList<VehicleModel> getPossibleModels() {
		return new ArrayList<VehicleModel>(possibleModels);
	}
	
	/**
	 * Checks if order can be completed on this assembly line.
	 * 
	 * @param order
	 * 		The order for which will be checked.
	 * @return True if the model of order is in the list of possibleModels or if the model or the order is null. Otherwise false.
	 */
	public boolean canDoOrder(Order order){
		if (order == null)
			return true;
		VehicleModel model = order.getConfiguration().getModel();
		return model == null || this.getPossibleModels().contains(model);
	}
}
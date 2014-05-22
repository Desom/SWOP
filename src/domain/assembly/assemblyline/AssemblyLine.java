package domain.assembly.assemblyline;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.InternalFailureException;
import domain.assembly.assemblyline.status.AssemblyLineStatus;
import domain.assembly.workstations.AssemblyTask;
import domain.assembly.workstations.VehicleAssemblyProcess;
import domain.assembly.workstations.Workstation;
import domain.assembly.workstations.WorkstationObserver;
import domain.configuration.models.VehicleModel;
import domain.scheduling.order.Order;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.AssemblyLineSchedulerObserver;

public class AssemblyLine implements WorkstationObserver, AssemblyLineSchedulerObserver {

	private ArrayList<Workstation> workstations;
	private final AssemblyLineScheduler assemblyLineScheduler;
	private AssemblyLineStatus currentStatus;
	private ArrayList<VehicleModel> possibleModels;
	private ArrayList<AssemblyLineObserver> observers;

	/**
	 * Constructor of AssemblyLine.
	 * 
	 * @param workstations
	 * 		The workstations of this assembly line.
	 * @param assemblyLineScheduler
	 * 		The scheduler of this assembly line.
	 * @param currentStatus
	 * 		The current status of this assembly line.
	 * @param possibleModels
	 * 		Only these VehicleModels will be able to be built on this assembly line.
	 */
	public AssemblyLine(ArrayList<Workstation> workstations, AssemblyLineScheduler assemblyLineScheduler, AssemblyLineStatus currentStatus, ArrayList<VehicleModel> possibleModels){
		if (workstations.isEmpty() || workstations == null)
			throw new IllegalArgumentException("Assembly line has been given no workstations.");
		this.addWorkstations(workstations);
		this.assemblyLineScheduler = assemblyLineScheduler;
		this.assemblyLineScheduler.setAssemblyLine(this);
		this.assemblyLineScheduler.addObserver(this);
		this.possibleModels = new ArrayList<VehicleModel>(possibleModels);
		this.currentStatus = currentStatus;
		this.observers = new ArrayList<AssemblyLineObserver>();
		try {
			this.advanceLine();
		} catch (CannotAdvanceException e) {
			//geen probleem dan moet de assemblyLine maar wachten.
		}
	}

	/**
	 * Returns all workstations on this assembly line.
	 * 
	 * @return A linked list containing all the workStations.
	 */
	public LinkedList<Workstation> getAllWorkstations(){
		return new LinkedList<Workstation>(workstations);
	}

	/**
	 * Returns the scheduler of this assembly line.
	 * 
	 * @return The scheduler of this assembly line.
	 */
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

	/**
	 * Returns all workstations that block the advance of the assembly line.
	 * 
	 * @return all workstations that block the advance of the assembly line.
	 */
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
	 * 		If the assembly line cannot advance.
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

	/**
	 * Creates a view of the current status of the assembly line.
	 * 
	 * @return An AssemblyStatusView representing the current status.
	 */
	public AssemblyStatusView getAssemblyLineView(){
		AssemblyStatusView view = new AssemblyStatusView(workstations, "Current Status");
		return view;
	}

	/**
	 * Returns the number of workstation on this assembly line.
	 * 
	 * @return The number of workstations on the assembly line.
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
	 * Checks if the assembly line is empty or not.
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
	 * @throws CannotAdvanceException TODO
	 */
	public void setCurrentStatus(AssemblyLineStatus newStatus) {
		this.currentStatus = newStatus;
		this.notifyObservers();
		if(this.canAdvanceLine()) {
			try {
				this.advanceLine();
			}
			catch(CannotAdvanceException e) {
				// TODO InternalFailureException gooien?
			}
		}
	}

	/**
	 * Returns the current status of this assembly line.
	 * 
	 * @return the current status of this assembly line.
	 */
	public AssemblyLineStatus getCurrentStatus() {
		return this.currentStatus;
	}

	/**
	 * Checks if this assembly line can accept new orders.
	 * 
	 * @return True if this assembly line can accept new orders, otherwise false.
	 */
	public Boolean canAcceptNewOrders() {
		return this.currentStatus.canAcceptNewOrders();
	}

	/**
	 * Returns the list of orders representing the assembly line state at the time the assembly line will accept new orders again.
	 * 
	 * @return The list of orders representing the assembly line state at the time the assembly line will accept new orders again.
	 */
	public LinkedList<Order> stateWhenAcceptingOrders() {
		return this.currentStatus.stateWhenAcceptingOrders(this);
	}

	/**
	 * Returns the time when the assembly line is accepting new orders again.
	 * 
	 * @return The time when the assembly line is accepting new orders again.
	 */
	public GregorianCalendar timeWhenAcceptingOrders() {
		return this.currentStatus.timeWhenAcceptingOrders(this);
	}

	/**
	 * TODO
	 * Adds the given workstation to the end of the assembly line.
	 * The given workstation can't belong to another assembly line. 
	 * 
	 * @param workstation
	 * 		The workstations to be added.
	 * TODO private liefst, maar wordt nog gebruikt in andere klassen
	 */
	protected void addWorkstations(ArrayList<Workstation> workstations){
		this.workstations = new ArrayList<Workstation>();
		for (Workstation workstation : workstations) {
			this.workstations.add(workstation);
			workstation.addObserver(this);
		}
	}

	/**
	 * Add all the given workstations to the end of the assembly line.
	 * The given workstation can't belong to other assembly lines. 
	 * 
	 * @param workstations
	 * 		The workstations to be added.
	 * TODO
	 */
//	protected void addAllWorkstation(List<Workstation> workstations){
//		for(Workstation w:workstations){
//			addWorkstation(w);
//		}
//	}

	/**
	 * Notifies the assembly line that something it observes has changed.
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
	 * 		The order which will be checked.
	 * @return True if the order is null or if the model of order is in the list of possibleModels
	 * 			and all tasks in this model can be handled on the workstations this assemblyline possesses.
	 * 			 Otherwise false.
	 */
	public boolean canDoOrder(Order order){
		if (order == null){
			return true;			
		}
		VehicleModel model = order.getConfiguration().getModel();
		if(!this.getPossibleModels().contains(model)){
			return false;			
		}
		if(model == null || this.getPossibleModels().contains(model)){
			for(AssemblyTask task : order.getAssemblyprocess().getAssemblyTasks()){
				boolean found = false;
				for(Workstation w : getAllWorkstations()){
					if(w.getTaskTypes().contains(task.getType())){
						found = true;
					}
				}
				if(!found)
					return false;
			}
		}
		return true;
	}

	/**
	 * Returns all possible status of this assembly line.
	 * 
	 * @return All possible status of this assembly line.
	 */
	public ArrayList<AssemblyLineStatus> getPossibleStatuses() {
		return this.currentStatus.getPossibleStatuses();
	}

	/**
	 * Adds an observer for this assembly line.
	 * 
	 * @param observer
	 * 		The new observer for this assembly line.
	 */
	public void addObserver(AssemblyLineObserver observer) {
		this.observers.add(observer);
	}

	/**
	 * Notifies its observers that it has changed.
	 */
	private void notifyObservers() {
		for (AssemblyLineObserver observer : this.observers)
			observer.update();
	}
}
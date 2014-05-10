package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.InternalFailureException;
import domain.configuration.OptionType;
import domain.order.Order;

public class AssemblyLine {

	private ArrayList<Workstation> workstations = null;
	private final AssemblyLineScheduler assemblyLineScheduler;
	private AssemblyLineStatus assemblyLineStatus;

	/**
	 * Constructor for the assembly line class.
	 * This constructor is also responsible for the creation of 3 workstations.
	 */
	public AssemblyLine(AssemblyLineScheduler assemblyLineScheduler){
		this.workstations = createWorkstations();
		this.assemblyLineScheduler = assemblyLineScheduler;
		this.assemblyLineScheduler.setAssemblyLine(this);
		this.assemblyLineStatus = new OperationalStatus();
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
		return this.assemblyLineStatus.canAdvanceLine(this);
	}

	ArrayList<Workstation> getBlockingWorkstations() {
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
		this.assemblyLineStatus.advanceLine(this);
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
	 * This method creates 3 workstations, specifies their ID's and the respective assembly task types those workstations can perform.
	 * 
	 * @return A list of the created workstations.
	 */
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
	}

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
			if(w.getCarAssemblyProcess() != null){
				orders.addLast(w.getCarAssemblyProcess().getOrder());
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
	@SuppressWarnings("unchecked")
	public int calculateTimeTillEmptyFor(LinkedList<Order> assembly) {
		LinkedList<Order> simulAssembly = (LinkedList<Order>) assembly.clone();
		int time = 0;
		for(int i = 0; i < 3; i++){
			time += this.calculateTimeTillAdvanceFor(simulAssembly);
			simulAssembly.removeLast();
			simulAssembly.addFirst(null);
		}
		return time;
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
		for(int j = 0; j < 3; j++){
			if(assembly.get(j) != null
					&& (this.filterWorkstations(assembly.get(j).getAssemblyprocess())).contains(allWorkstations.get(j))
					&& assembly.get(j).getConfiguration().getExpectedWorkingTime() > maxTime){
				maxTime = assembly.get(j).getConfiguration().getExpectedWorkingTime();
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
			if(workstation.getCarAssemblyProcess() != null){
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns all workstations which can complete tasks of the given car assembly process.
	 * 
	 * @param process
	 * 		The car assembly process to be checked.
	 * @return All workstations required to complete all the tasks of this car assembly process.
	 */
	public ArrayList<Workstation> filterWorkstations(CarAssemblyProcess process){
		ArrayList<Workstation> filteredWorkstations = new ArrayList<Workstation>();
		for(Workstation workstation : workstations){
			if(workstation.compatibleWith(process).size() > 0){
				filteredWorkstations.add(workstation);
			}
		}
		return filteredWorkstations;
	}

	void setStatus(AssemblyLineStatus status) {
		this.assemblyLineStatus = status;		
	}
	public Boolean canAcceptNewOrders() {
		return this.assemblyLineStatus.canAcceptNewOrders();
	}
	public LinkedList<Order> StateWhenAcceptingOrders() {
		return this.assemblyLineStatus.StateWhenAcceptingOrders(this);
	}


	public GregorianCalendar TimeWhenAcceptingOrders(AssemblyLine assemblyLine) {
		return this.assemblyLineStatus.TimeWhenAcceptingOrders(this);
	}
}
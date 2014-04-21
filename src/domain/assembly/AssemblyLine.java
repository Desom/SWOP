package domain.assembly;
import java.util.ArrayList;
import java.util.LinkedList;

import domain.InternalFailureException;
import domain.Statistics;
import domain.configuration.OptionType;
import domain.order.Order;
import domain.user.CarMechanic;


public class AssemblyLine {

	private ArrayList<Workstation> workstations = null;
	private final AssemblyLineScheduler assemblyLineScheduler;
	private final Statistics statistics;

	/**
	 * Constructor for the assembly line class.
	 * This constructor is also responsible for the creation of 3 workstations.
	 */
	public AssemblyLine(AssemblyLineScheduler assemblyLineScheduler, Statistics statistics){
		this.statistics = statistics;
		this.workstations = createWorkstations();
		this.assemblyLineScheduler = assemblyLineScheduler;
		this.assemblyLineScheduler.setAssemblyLine(this);
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
	 * 
	 * @throws DoesNotExistException
	 * @throws CannotAdvanceException 
	 * 				If there are workstations that are blocking the assembly line.
	 * @throws InternalFailureException 
	 * 				If a fatal error occurred the program could not recover from.
	 */
	public void advanceLine() throws CannotAdvanceException, InternalFailureException{
		// check of alle tasks klaar zijn, zoniet laat aan de user weten welke nog niet klaar zijn (zie exception message).
		boolean isReady = true;
		CannotAdvanceException cannotAdvance = new CannotAdvanceException();
		for(Workstation w : getAllWorkstations()){
			if(!w.hasAllTasksCompleted()){
				isReady = false;
				cannotAdvance.addBlockingWorkstation(w);
			}
		}
		if(!isReady){
			throw cannotAdvance;
		}
		try{
			// move huidige cars 1 plek
			//neem CarOrder van WorkStation 3
			Workstation workstationLast = selectWorkstationById(getNumberOfWorkstations());
			Order finished = null;
			if(workstationLast.getCarAssemblyProcess() != null){
				// zoek welke CarOrder klaar is, wacht met het zetten van de deliveryTime omdat de tijd van het schedule nog moet worden geupdate.
				finished = workstationLast.getCarAssemblyProcess().getOrder();
			}
			for(int i = getAllWorkstations().size(); i>1; i--){
				Workstation workstationNext = selectWorkstationById(i);
				workstationNext.clear();;
				Workstation workstationPrev = selectWorkstationById(i-1);
				workstationNext.setCarAssemblyProcess(workstationPrev.getCarAssemblyProcess());
				if(workstationNext.getCarAssemblyProcess() != null){
					for(AssemblyTask t : workstationNext.getCarAssemblyProcess().compatibleWith(workstationNext)){
						workstationNext.addAssemblyTask(t);
					}
				}
			}

			//zoek de tijd die nodig was om alle tasks uit te voeren.
			int timeSpendForTasks = 0;
			for(Workstation workstation : this.getAllWorkstations()){
				if(workstation.getTimeSpend() > timeSpendForTasks)
					timeSpendForTasks = workstation.getTimeSpend();
			}

			//voeg nieuwe car toe.
			Order newOrder = this.assemblyLineScheduler.getNextOrder(timeSpendForTasks);
			CarAssemblyProcess newCar = null;
			if(newOrder != null){
				newCar = newOrder.getAssemblyprocess();
			}

			Workstation workstation1 = selectWorkstationById(1);
			workstation1.clear();;
			workstation1.setCarAssemblyProcess(newCar);
			//TODO moet dit niet automatisch gebeuren wanneer je workstation een CarAssemblyProcess geeft?
			if(newCar != null){
				for(AssemblyTask t : newCar.compatibleWith(workstation1)){
					workstation1.addAssemblyTask(t);
				}
			}

			if(finished != null){
				finished.setDeliveredTime(this.assemblyLineScheduler.getCurrentTime());
				finished.registerDelay(getAllWorkstations());
				this.statistics.update();
			}
		}
		catch(DoesNotExistException e){
			throw new InternalFailureException("Suddenly a Workstation disappeared while that should not be possible.");
		}
	}

	/**
	 * Add's the specified user to the workstation with the specified ID
	 * 
	 * @param user The user that is to be added to the workstation
	 * @param workStation_id The ID of the workstation the user should be added to.
	 * @throws InternalFailureException 
	 * @throws DoesNotExistException 
	 * @throws Exception If the Carmechanic could not be appointed to the workstation.
	 */
	/*public void selectWorkstation(User user, int workStation_id) throws UserAccessException, InternalFailureException{
		if(user.canPerform("selectWorkstation")){
			Workstation selected;
			selected = selectWorkstationById(workStation_id, user);
			selected.addCarMechanic(user);
		}else{
			throw new UserAccessException(user, "advanceLine");
		}
	}*/

	/**
	 * Selects the workstation with the specified id from the list of all workstations
	 * 
	 * @param id The ID of the desired workstation
	 * @return The workstation that matches the specified ID
	 * @throws DoesNotExistException when no workstation with the specified ID exists.
	 */
	public Workstation selectWorkstationById(int id) throws DoesNotExistException{
		Workstation selected = null;
		for(Workstation w : getAllWorkstations()){
			if(w.getId() == id)
				selected = w;
		}
		if(selected == null)
			throw new DoesNotExistException("No workstation exists with ID: " + id);
		// heb dit veranderd van InternalFailureException naar DoesNotExistException omdat InternalFailureException niet van toepassing leek.
		return selected;
	}

	/**
	 * This method creates 3 workstations, specifies their ID's and the respective assembly task types those workstations can perform.
	 * 
	 * @return	a linked hashmap with the workstations as keys and a car assembly process as value
	 */
	private ArrayList<Workstation> createWorkstations(){
		ArrayList<Workstation> list = new ArrayList<Workstation>();
		ArrayList<OptionType> taskTypes1 = new ArrayList<OptionType>();
		taskTypes1.add(OptionType.Body);
		taskTypes1.add(OptionType.Color);
		Workstation workStation1 = new Workstation(this, 1, taskTypes1);

		ArrayList<OptionType> taskTypes2 = new ArrayList<OptionType>();
		taskTypes2.add(OptionType.Engine);
		taskTypes2.add(OptionType.Gearbox);
		Workstation workStation2 = new Workstation(this, 2, taskTypes2);

		ArrayList<OptionType> taskTypes3 = new ArrayList<OptionType>();
		taskTypes3.add(OptionType.Seats);
		taskTypes3.add(OptionType.Airco);
		taskTypes3.add(OptionType.Wheels);
		taskTypes3.add(OptionType.Spoiler);
		Workstation workStation3 = new Workstation(this, 3, taskTypes3);
		
		list.add(workStation1);
		list.add(workStation2);
		list.add(workStation3);
		return list;
	}

	/**
	 * Creates a view of the current status of the assembly line
	 * 
	 * @param user The user requesting the assemblyStatusview
	 * @return An AssemblyStatusView representing the current status
	 */
	public AssemblyStatusView currentStatus(){
		AssemblyStatusView view = new AssemblyStatusView(workstations, "Current Status");
		return view;
	}


	/**
	 * Creates a view of the future status of the assembly line
	 * 
	 * @param user 
	 * 			The user requesting the assemblyStatusview.
	 * @param time 
	 * 			The time that has past since the last advanceLine.
	 * @return An AssemblyStatusView representing the future status
	 * @throws InternalFailureException
	 * 			If a fatal error occurred the program could not recover from.
	 */
	public AssemblyStatusView futureStatus(int time) throws InternalFailureException{
		// check if the line can advance
		boolean isReady = true;
		for(Workstation w : getAllWorkstations()){
			if(!w.hasAllTasksCompleted()){
				isReady = false;
			}
		}
		ArrayList<Workstation> fakeWorkstations = new ArrayList<Workstation>();

		if(!isReady){ 
			// if the line cannot advance, return the current status, because that is equal to the future status
			return currentStatus();
		}
		try{
			// if the line can advance make new workstations representing the future
			ArrayList<Workstation> list = new ArrayList<Workstation>(createWorkstations());
			for(Workstation fake: list){ // set the corresponding car mechanics.
				Workstation real = selectWorkstationById(fake.getId());
				try{
					fake.addCarMechanic(real.getCarMechanic());
				}catch(IllegalStateException e){}
				if(fake.getId() != 1){
					Workstation realPrev = selectWorkstationById(fake.getId()-1);
					fake.setCarAssemblyProcess(realPrev.getCarAssemblyProcess());
					try{
						for(AssemblyTask t : fake.getCarAssemblyProcess().compatibleWith(fake)){
							fake.addAssemblyTask(t);
						}
					}
					catch(NullPointerException e){}
				}else{
					Order order = this.assemblyLineScheduler.seeNextOrder(time);
					if(order != null){
						CarAssemblyProcess futureCar = order.getAssemblyprocess();
						fake.setCarAssemblyProcess(futureCar);
						for(AssemblyTask t : futureCar.compatibleWith(fake)){
							fake.addAssemblyTask(t);
						}
					}else{
						fake.setCarAssemblyProcess(null);
					}
				}
			}
			AssemblyStatusView view = new AssemblyStatusView(fakeWorkstations, "Future Status");
			return view;
		}
		catch(DoesNotExistException e){
			throw new InternalFailureException("Suddenly a Workstation disappeared while that could not be possible.");
		}

	}

	/**
	 * Get an array of all workstation id's
	 * @return all workstation id's
	 */
	public LinkedList<Integer> getWorkstationIDs(){
		LinkedList<Integer> ids= new LinkedList<Integer>();
		for(Workstation w: workstations){
			ids.add(w.getId());
		}
		return ids;
	}

	/**
	 * 
	 * @return The number of workstations on the assembly line
	 */
	public int getNumberOfWorkstations(){
		return workstations.size();
	}

	/**
	 * Returns a list of the Orders the Workstations are working on.
	 * 
	 * @return a list of all orders currently in the respective Workstation as
	 *         indicated by the position in the list. (null if Workstation is
	 *         empty.)
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
	 * This method is only to be used for testing. It will complete all tasks the workstations are currently working on.
	 * It will complete those tasks in a way where the time spent on each workstation is the expected time for that specific car order.
	 * When the last workstation finishes it's last task the line will ofcourse automatically advance.
	 * 
	 * @throws InternalFailureException 
	 * @throws IllegalStateException 
	 */
	private void fullDefaultAdvance() throws IllegalStateException, InternalFailureException{
		LinkedList<Workstation> wList = getAllWorkstations();
		for(Workstation w: wList){ // filter the already completed workstations so the line won't accidentally advance twice.
			if(w.hasAllTasksCompleted()){
				wList.remove(w);
			}
		}
		for(Workstation w : wList){
			CarMechanic mechanic = w.getCarMechanic();
			if(mechanic == null)
				mechanic = new CarMechanic(100*w.getId()); // randomize ID een beetje
			while(w.getAllPendingTasks().size() > 1){
				w.selectTask(w.getAllPendingTasks().get(0));
				w.completeTask(mechanic,0);
			}
			if(w.getAllPendingTasks().size() != 0){
				w.selectTask(w.getAllPendingTasks().get(0));
				w.completeTask(mechanic,w.getCarAssemblyProcess().getOrder().getConfiguration().getExpectedWorkingTime());
			}
		}
	}

	/**
	 * Calculates the estimated time necessary to empty the assemblyLine, based on the given orders.
	 * 
	 * @param assembly
	 * 		A list of Orders which represents the assemblyLine. ( null if there is no order on that respective Workstation.)
	 * @return The calculated amount of minutes it will take to empty the given assemblyLine; 
	 */
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
	 *            The Orders that are on the assemblyLine.
	 * @return The amount of minutes it will take to complete all tasks on all workstations.
	 */
	public int calculateTimeTillAdvanceFor(LinkedList<Order> assembly) {
		LinkedList<Workstation> allWorkstations = this.getAllWorkstations();
		int maxTime = 0;
		for(int j = 0; j < 3; j++){
			if(assembly.get(j) != null
					&& (assembly.get(j).getAssemblyprocess().filterWorkstations(allWorkstations)).contains(allWorkstations.get(j))
					&& assembly.get(j).getConfiguration().getExpectedWorkingTime() > maxTime){
				maxTime = assembly.get(j).getConfiguration().getExpectedWorkingTime();
			}
		}
		return maxTime;
	}

	public boolean isEmpty() {
		for(Workstation w : getAllWorkstations()){
			if(w.getCarAssemblyProcess() != null){
				return false;
			}
		}
		return true;
	}

}

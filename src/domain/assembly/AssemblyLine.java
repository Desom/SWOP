package domain.assembly;
import java.util.ArrayList;
import java.util.LinkedList;

import domain.InternalFailureException;
import domain.Statistics;
import domain.configuration.OptionType;
import domain.order.Order;


public class AssemblyLine {

	private ArrayList<Workstation> workstations = null;
	private final Scheduler schedule;
	private final Statistics statistics;

	/**
	 * Constructor for the assembly line class.
	 * This constructor is also responsible for the creation of 3 workstations.
	 */
	public AssemblyLine(Scheduler schedule, Statistics statistics){
		this.statistics = statistics;
		this.workstations = createWorkstations();
		this.schedule = schedule;
	}

	/**
	 * 
	 * @return A linked list containing all the workStations.
	 */
	public LinkedList<Workstation> getAllWorkstations(){
		return new LinkedList<Workstation>(workstations);
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
			CarAssemblyProcess newCar = null;
			if(this.schedule.seeNextCarOrder(timeSpendForTasks) != null){
				newCar = this.schedule.getNextCarOrder(timeSpendForTasks).getAssemblyprocess();
			}

			Workstation workstation1 = selectWorkstationById(1);
			workstation1.clear();;
			workstation1.setCarAssemblyProcess(newCar);
			if(newCar != null){
				for(AssemblyTask t : newCar.compatibleWith(workstation1)){
					workstation1.addAssemblyTask(t);
				}
			}

			if(finished != null){
				finished.setDeliveredTime(this.schedule.getCurrentTime());
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
					Order order = this.schedule.seeNextCarOrder(time);
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
	 * Returns a list of all orders the assemblyLine is currently working on.
	 * 
	 * @return a list of all orders currently on the assemblyLine
	 */
	public ArrayList<Order> getAllOrders() {
		ArrayList<Order> orders = new ArrayList<Order>();
		for(Workstation w : getAllWorkstations()){
			if(w.getCarAssemblyProcess() != null){
				orders.add(w.getCarAssemblyProcess().getOrder());
			}
		}
		return orders;
	}

}

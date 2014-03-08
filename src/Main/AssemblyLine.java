package Main;
import java.util.ArrayList;
import java.util.LinkedList;


public class AssemblyLine {

	private LinkedList<Workstation> workStations = null;
	private final ProductionSchedule schedule;

	/**
	 * Constructor for the assembly line class.
	 * This constructor is also responsible for the creation of 3 workstations.
	 */
	public AssemblyLine(ProductionSchedule schedule){
		createWorkStations();
		this.schedule = schedule;
	}

	/**
	 * 
	 * @return A linked list containing all the workStations.
	 * @throws UserAccessException If this user is not allowed to execute this method.
	 */
	@SuppressWarnings("unchecked")
	protected LinkedList<Workstation> getAllWorkStations(User user) throws UserAccessException{
		if(user.canPerform("getAllWorkStations")){
			return (LinkedList<Workstation>) workStations.clone();
		}else{
			throw new UserAccessException(user, "getAllWorkStations");
		}
	}
	
	/**
	 * 
	 * @param user The user trying to advance the line
	 * @param time The time of the previous iteration. (eg the last work took 45 min instead of an hour)
	 * @throws UserAccessException
	 * @throws DoesNotExistException
	 * @throws CannotAdvanceException if there are workstations that are blocking the assembly line.
	 */
	public void advanceLine(User user, int time) throws UserAccessException, DoesNotExistException, CannotAdvanceException{
		if(user.canPerform("advanceLine")){
			// check of alle tasks klaar zijn, zoniet laat aan de user weten welke nog niet klaar zijn (zie exception message).
			boolean isReady = true;
			CannotAdvanceException cannotAdvance = new CannotAdvanceException();
			for(Workstation w : getAllWorkStations(user)){
				if(!w.hasAllTasksCompleted(user)){
					isReady = false;
					cannotAdvance.addBlockingWorkstation(w);
				}
			}
			if(isReady){
				// move huidige cars 1 plek
				
				//voeg nieuwe car toe.
				CarAssemblyProcess newCar = this.schedule.getNextCarOrder(time).getCar().getAssemblyprocess();
				Workstation workstation1 = selectWorkStationId(1, user);
				for(AssemblyTask t : newCar.compatibleWith(workstation1)){
					workstation1.addAssemblyTask(user, t);
				}
			}else{
				throw cannotAdvance;
			}
		}else{
			throw new UserAccessException(user, "advanceLine");
		}
	}

	/**
	 * Add's the specified user to the workstation with the specified ID
	 * 
	 * @param user The user that is to be added to the workstation
	 * @param workStation_id The ID of the workstation the user should be added to.
	 * @throws Exception If the Carmechanic could not be appointed to the workstation.
	 */
	protected void selectWorkStation(User user, int workStation_id) throws Exception{
		if(user.canPerform("selectWorkStation")){
			Workstation selected = selectWorkStationId(workStation_id, user);
			selected.addCarMechanic(user);
		}else{
			throw new UserAccessException(user, "advanceLine");
		}
	}

	/**
	 * Selects the workstation with the specified id from the list of all workstations
	 * 
	 * @param id The ID of the desired workstation
	 * @return The workstation that matches the specified ID
	 * @throws DoesNotExistException when no workstation with the specified ID exists.
	 * @throws UserAccessException 
	 */
	protected Workstation selectWorkStationId(int id, User user) throws DoesNotExistException, UserAccessException{
		if(user.canPerform("selectWorkStation")){
			Workstation selected = null;
			for(Workstation w : getAllWorkStations(user)){
				if(w.getId() == id)
					selected = w;
			}
			if(selected == null)
				throw new DoesNotExistException("No workstation exists with ID: " + id);
			return selected;
		}else{
			throw new UserAccessException(user, "selectWorkStation");
		}
	}

	/**
	 * This method creates 3 workstations, specifies their ID's and the respective assembly tasks those workstations can perform.
	 */
	private void createWorkStations(){
		ArrayList<String> taskTypes1 = new ArrayList<String>();
		taskTypes1.add("Body");
		taskTypes1.add("Color");
		Workstation workStation1 = new Workstation(1, taskTypes1);
		
		ArrayList<String> taskTypes2 = new ArrayList<String>();
		taskTypes2.add("Engine");
		taskTypes2.add("GearBox");
		Workstation workStation2 = new Workstation(2, taskTypes2);
		
		ArrayList<String> taskTypes3 = new ArrayList<String>();
		taskTypes3.add("Seats");
		taskTypes3.add("Airco");
		taskTypes3.add("Wheels");
		Workstation workStation3 = new Workstation(3, taskTypes3);
		
		this.workStations.add(workStation1);
		this.workStations.add(workStation2);
		this.workStations.add(workStation3);
	}
}

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
		workStations = new LinkedList<Workstation>(createWorkStations());
		this.schedule = schedule;
	}

	/**
	 * 
	 * @return A linked list containing all the workStations.
	 * @throws UserAccessException If this user is not allowed to execute this method.
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<Workstation> getAllWorkStations(User user) throws UserAccessException{
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
				for(int i = getAllWorkStations(user).size(); i>1; i--){
					Workstation workstationNext = selectWorkStationId(i, user);
					workstationNext.clearCar();
					Workstation workstationPrev = selectWorkStationId(i-1, user);
					workstationNext.setCurrentCar(workstationPrev.getCurrentCar());
					for(AssemblyTask t : workstationNext.getCurrentCar().compatibleWith(workstationNext)){
						workstationNext.addAssemblyTask(user, t);
					}
				}
				
				//voeg nieuwe car toe.
				CarAssemblyProcess newCar = this.schedule.getNextCarOrder(time).getCar().getAssemblyprocess();
				Workstation workstation1 = selectWorkStationId(1, user);
				workstation1.clearCar();
				workstation1.setCurrentCar(newCar);
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
	 * @throws UserAccessException 
	 * @throws DoesNotExistException 
	 * @throws Exception If the Carmechanic could not be appointed to the workstation.
	 */
	public void selectWorkStation(User user, int workStation_id) throws UserAccessException, DoesNotExistException {
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
	public Workstation selectWorkStationId(int id, User user) throws DoesNotExistException, UserAccessException{
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
	private ArrayList<Workstation> createWorkStations(){
		ArrayList<Workstation> list = new ArrayList<Workstation>();
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
	 * @throws UserAccessException if the user is not allowed to invoke this method
	 */
	public AssemblyStatusView currentStatus(User user) throws UserAccessException{
		if(user.canPerform("currentStatus")){
			ArrayList<Workstation> list = new ArrayList<Workstation>(getAllWorkStations(user));
			AssemblyStatusView view = new AssemblyStatusView(list, "Current Status");
			return view;
		}else{
			throw new UserAccessException(user, "currentStatus");
		}
	}
	
	
	/**
	 * Creates a view of the future status of the assembly line
	 * 
	 * @param user The user requesting the assemblyStatusview
	 * @return An AssemblyStatusView representing the future status
	 * @throws UserAccessException if the user is not allowed to invoke this method
	 * @throws DoesNotExistException If a workstation with a non existing ID is requested
	 */
	public AssemblyStatusView futureStatus(User user) throws UserAccessException, DoesNotExistException{
		if(user.canPerform("futureStatus")){
			// check if the line can advance
			boolean isReady = true;
			for(Workstation w : getAllWorkStations(user)){
				if(!w.hasAllTasksCompleted(user)){
					isReady = false;
				}
			}
			if(isReady){ // if the line can advance make new workstations representing the future
				ArrayList<Workstation> list = new ArrayList<Workstation>(createWorkStations());
				for(Workstation fake: list){ // set the corresponding car mechanics.
					Workstation real = selectWorkStationId(fake.getId(), user);
					fake.addCarMechanic(real.getCarMechanic());
					if(fake.getId() != 1){
						Workstation realPrev = selectWorkStationId(fake.getId()-1, user);
						fake.setCurrentCar(realPrev.getCurrentCar());
						for(AssemblyTask t : fake.getCurrentCar().compatibleWith(fake)){
							fake.addAssemblyTask(user, t);
						}
					}else{
						CarAssemblyProcess futureCar = this.schedule.seeNextCarOrder().getCar().getAssemblyprocess();
						fake.setCurrentCar(futureCar);
						for(AssemblyTask t : futureCar.compatibleWith(fake)){
							fake.addAssemblyTask(user, t);
						}
					}
				}
				AssemblyStatusView view = new AssemblyStatusView(list, "Future Status");
				return view;
			}else{ // if the line cannot advance, return the current status, because that is equal to the future status
				return currentStatus(user);
			}
		}else{
			throw new UserAccessException(user, "currentStatus");
		}
	}
}

package Assembly;
import java.util.ArrayList;
import java.util.LinkedList;

import Car.CarOrder;
import Main.InternalFailureException;
import User.User;
import User.UserAccessException;


public class AssemblyLine {

	private LinkedList<Workstation> workstations = null;
	private final ProductionSchedule schedule;

	/**
	 * Constructor for the assembly line class.
	 * This constructor is also responsible for the creation of 3 workstations.
	 */
	public AssemblyLine(ProductionSchedule schedule){
		workstations = new LinkedList<Workstation>(createWorkstations());
		this.schedule = schedule;
	}

	/**
	 * 
	 * @return A linked list containing all the workStations.
	 * @throws UserAccessException If this user is not allowed to execute this method.
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<Workstation> getAllWorkstations(User user) throws UserAccessException{
		if(user.canPerform("getAllWorkstations")){
			return (LinkedList<Workstation>) workstations; //TODO GJ
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
	 * @throws InternalFailureException 
	 */
	public void advanceLine(User user, int time) throws UserAccessException, CannotAdvanceException, InternalFailureException{
		if(user.canPerform("advanceLine")){
			// check of alle tasks klaar zijn, zoniet laat aan de user weten welke nog niet klaar zijn (zie exception message).
			boolean isReady = true;
			CannotAdvanceException cannotAdvance = new CannotAdvanceException();
			for(Workstation w : getAllWorkstations(user)){
				if(!w.hasAllTasksCompleted(user)){
					isReady = false;
					cannotAdvance.addBlockingWorkstation(w);
				}
			}
			if(isReady){
				// move huidige cars 1 plek
				//neem CarOrder van WorkStation 3
				Workstation workstationLast = selectWorkstationById(getNumberOfWorkstations(), user);
				if(workstationLast.getCurrentCar() != null){
					CarOrder finished = workstationLast.getCurrentCar().getCar().getOrder();
					finished.setDeliveredTime(user, this.schedule.getCurrentTime());
				}
				for(int i = getAllWorkstations(user).size(); i>1; i--){
					Workstation workstationNext = selectWorkstationById(i, user);
					workstationNext.clearCar();
					Workstation workstationPrev = selectWorkstationById(i-1, user);
					workstationNext.setCurrentCar(workstationPrev.getCurrentCar());
					if(workstationNext.getCurrentCar() != null){
						for(AssemblyTask t : workstationNext.getCurrentCar().compatibleWith(workstationNext)){
							workstationNext.addAssemblyTask(user, t);
						}
					}
				}
				
				//voeg nieuwe car toe.
				CarAssemblyProcess newCar = this.schedule.getNextCarOrder(time).getCar().getAssemblyprocess();
				Workstation workstation1 = selectWorkstationById(1, user);
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
	 * @throws InternalFailureException 
	 * @throws DoesNotExistException 
	 * @throws Exception If the Carmechanic could not be appointed to the workstation.
	 */
	public void selectWorkstation(User user, int workStation_id) throws UserAccessException, InternalFailureException{
		if(user.canPerform("selectWorkstation")){
			Workstation selected;
			selected = selectWorkstationById(workStation_id, user);
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
	 * @throws InternalFailureException 
	 */
	public Workstation selectWorkstationById(int id, User user) throws UserAccessException, InternalFailureException{
		if(user.canPerform("selectWorkstationID")){
			Workstation selected = null;
			for(Workstation w : getAllWorkstations(user)){
				if(w.getId() == id)
					selected = w;
			}
			if(selected == null)
				throw new InternalFailureException("No workstation exists with ID: " + id);
			return selected;
		}else{
			throw new UserAccessException(user, "selectWorkStationID");
		}
	}

	/**
	 * This method creates 3 workstations, specifies their ID's and the respective assembly tasks those workstations can perform.
	 */
	private ArrayList<Workstation> createWorkstations(){
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
			ArrayList<Workstation> list = new ArrayList<Workstation>(getAllWorkstations(user));
			AssemblyStatusView view = new AssemblyStatusView(user, list, "Current Status");
			return view;
		}else{
			throw new UserAccessException(user, "currentStatus");
		}
	}


	/**
	 * Creates a view of the future status of the assembly line
	 * 
	 * @param user The user requesting the assemblyStatusview
	 * @param time The time that has past since the last advanceLine
	 * @return An AssemblyStatusView representing the future status
	 * @throws UserAccessException if the user is not allowed to invoke this method
	 * @throws InternalFailureException 
	 * @throws DoesNotExistException If a workstation with a non existing ID is requested
	 */
	public AssemblyStatusView futureStatus(User user, int time) throws UserAccessException, InternalFailureException{
		if(user.canPerform("futureStatus")){
			// check if the line can advance
			boolean isReady = true;
			for(Workstation w : getAllWorkstations(user)){
				if(!w.hasAllTasksCompleted(user)){
					isReady = false;
				}
			}
			if(isReady){ // if the line can advance make new workstations representing the future
				ArrayList<Workstation> list = new ArrayList<Workstation>(createWorkstations());
				for(Workstation fake: list){ // set the corresponding car mechanics.
					Workstation real = selectWorkstationById(fake.getId(), user);
					fake.addCarMechanic(real.getCarMechanic());
					if(fake.getId() != 1){
						Workstation realPrev = selectWorkstationById(fake.getId()-1, user);
						fake.setCurrentCar(realPrev.getCurrentCar());
						for(AssemblyTask t : fake.getCurrentCar().compatibleWith(fake)){
							fake.addAssemblyTask(user, t);
						}
					}else{
						CarAssemblyProcess futureCar = this.schedule.seeNextCarOrder(time).getCar().getAssemblyprocess();
						fake.setCurrentCar(futureCar);
						for(AssemblyTask t : futureCar.compatibleWith(fake)){
							fake.addAssemblyTask(user, t);
						}
					}
				}
				AssemblyStatusView view = new AssemblyStatusView(user, list, "Future Status");
				return view;
			}else{ // if the line cannot advance, return the current status, because that is equal to the future status
				return currentStatus(user);
			}
		}else{
			throw new UserAccessException(user, "futureStatus");
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
}

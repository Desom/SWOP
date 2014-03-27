package Assembly;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import Car.CarOrder;
import Car.OptionType;
import Main.InternalFailureException;
import User.User;
import User.UserAccessException;


public class AssemblyLine {

	private LinkedHashMap<Workstation,CarAssemblyProcess> workstations = null;
	private final ProductionSchedule schedule;

	/**
	 * Constructor for the assembly line class.
	 * This constructor is also responsible for the creation of 3 workstations.
	 */
	public AssemblyLine(ProductionSchedule schedule){
		workstations = createWorkstations();
		this.schedule = schedule;
	}

	/**
	 * 
	 * @return A linked list containing all the workStations.
	 * @throws UserAccessException If this user is not allowed to execute this method.
	 */
	public LinkedList<Workstation> getAllWorkstations(User user) throws UserAccessException{
		if(user.canPerform("getAllWorkstations")){
			return new LinkedList<Workstation>(workstations.keySet());
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
				if(workstations.get(workstationLast) != null){
					CarOrder finished = workstations.get(workstationLast).getCar().getOrder();
					finished.setDeliveredTime(user, this.schedule.getCurrentTime());
				}
				for(int i = getAllWorkstations(user).size(); i>1; i--){
					Workstation workstationNext = selectWorkstationById(i, user);
					clearWorkstation(workstationNext);
					Workstation workstationPrev = selectWorkstationById(i-1, user);
					workstations.put(workstationNext, workstations.get(workstationPrev));
					if(workstations.get(workstationNext) != null){
						for(AssemblyTask t : workstations.get(workstationNext).compatibleWith(workstationNext)){
							workstationNext.addAssemblyTask(user, t);
						}
					}
				}
				
				//voeg nieuwe car toe.
				CarAssemblyProcess newCar = null;
				if(this.schedule.seeNextCarOrder(time) != null){
					newCar = this.schedule.getNextCarOrder(time).getCar().getAssemblyprocess();
				}
				
				Workstation workstation1 = selectWorkstationById(1, user);
				clearWorkstation(workstation1);
				workstations.put(workstation1, newCar);
				if(newCar != null){
					for(AssemblyTask t : newCar.compatibleWith(workstation1)){
						workstation1.addAssemblyTask(user, t);
					}
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
	 * This method creates 3 workstations, specifies their ID's and the respective assembly task types those workstations can perform.
	 * 
	 * @return	a linked hashmap with the workstations as keys and a car assembly process as value
	 */
	private LinkedHashMap<Workstation, CarAssemblyProcess> createWorkstations(){
		LinkedHashMap<Workstation, CarAssemblyProcess> list = new LinkedHashMap<Workstation, CarAssemblyProcess>();
		ArrayList<OptionType> taskTypes1 = new ArrayList<OptionType>();
		taskTypes1.add(OptionType.Body);
		taskTypes1.add(OptionType.Color);
		Workstation workStation1 = new Workstation(1, taskTypes1);

		ArrayList<OptionType> taskTypes2 = new ArrayList<OptionType>();
		taskTypes2.add(OptionType.Engine);
		taskTypes2.add(OptionType.Gearbox);
		Workstation workStation2 = new Workstation(2, taskTypes2);

		ArrayList<OptionType> taskTypes3 = new ArrayList<OptionType>();
		taskTypes3.add(OptionType.Seats);
		taskTypes3.add(OptionType.Airco);
		taskTypes3.add(OptionType.Wheels);
		taskTypes3.add(OptionType.Spoiler);
		Workstation workStation3 = new Workstation(3, taskTypes3);

		list.put(workStation1, null);
		list.put(workStation2, null);
		list.put(workStation3, null);
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
			AssemblyStatusView view = new AssemblyStatusView(user, workstations, "Current Status");
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
			LinkedHashMap<Workstation, CarAssemblyProcess> fakeWorkstations = new LinkedHashMap<Workstation, CarAssemblyProcess>();
			
			if(isReady){ // if the line can advance make new workstations representing the future
				ArrayList<Workstation> list = new ArrayList<Workstation>(createWorkstations().keySet());
				for(Workstation fake: list){ // set the corresponding car mechanics.
					Workstation real = selectWorkstationById(fake.getId(), user);
					try{
						fake.addCarMechanic(real.getCarMechanic());
					}catch(IllegalStateException e){}
					if(fake.getId() != 1){
						Workstation realPrev = selectWorkstationById(fake.getId()-1, user);
						fakeWorkstations.put(fake, workstations.get(realPrev));
						try{
							for(AssemblyTask t : fakeWorkstations.get(fake).compatibleWith(fake)){
								fake.addAssemblyTask(user, t);
							}
						}
						catch(NullPointerException e){}
					}else{
						//TODO NullpointerException indien geen car uit schedule komt, Exception gooien in ProductionSchedule?
						// of hier null checken?
						CarAssemblyProcess futureCar = this.schedule.seeNextCarOrder(time).getCar().getAssemblyprocess();
						fakeWorkstations.put(fake, futureCar);
						for(AssemblyTask t : futureCar.compatibleWith(fake)){
							fake.addAssemblyTask(user, t);
						}
					}
				}
				AssemblyStatusView view = new AssemblyStatusView(user, fakeWorkstations, "Future Status");
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
		for(Workstation w: workstations.keySet()){
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
	 * Clears the workstation of all tasks and the active task. Removes the associated car assembly process form the linked hash map.
	 * 
	 * @param workstation
	 */
	private void clearWorkstation(Workstation workstation) {
		workstation.clear();
		workstations.put(workstation, null);
	}
	
	public CarAssemblyProcess getCarAssemblyProcess(Workstation workstation) {
		return workstations.get(workstation);
	}
}

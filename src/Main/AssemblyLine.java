package Main;
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
	
	public void advanceLine(User user, int time) throws UserAccessException{
		if(user.canPerform("advanceLine")){
			//schedule.getNextCarOrder(time);
			System.out.println("DUMMY METHODE");
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
	 * @throws Exception when no workstation with the specified ID exists.
	 */
	protected Workstation selectWorkStationId(int id, User user) throws Exception{
		if(user.canPerform("selectWorkStation")){
			Workstation selected = null;
			for(Workstation w : getAllWorkStations(user)){
				if(w.getId() == id)
					selected = w;
			}
			if(selected == null)
				throw new Exception("No workstation exists with ID: " + id);
			return selected;
		}else{
			throw new UserAccessException(user, "selectWorkStation");
		}
	}

	/**
	 * This method creates 3 workstations, specifies their ID's and the respective assembly tasks those workstations can perform.
	 */
	private void createWorkStations(){
		Workstation workStation1 = new Workstation(1, null); // null moet een lijst van alle mogelijke tasks worden (als String)
		Workstation workStation2 = new Workstation(2, null); // null moet een lijst van alle mogelijke tasks worden (als String)
		Workstation workStation3 = new Workstation(3, null); // null moet een lijst van alle mogelijke tasks worden (als String)
		this.workStations.add(workStation1);
		this.workStations.add(workStation2);
		this.workStations.add(workStation3);
	}
}

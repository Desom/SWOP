package Main;
import java.util.LinkedList;


public class AssemblyLine {

	private LinkedList<Workstation> workStations = null;
	
	/**
	 * Constructor for the assembly line class.
	 * This constructor is also responsible for the creation of 3 workstations.
	 */
	public AssemblyLine(){
		createWorkStations();
	}
	
	/**
	 * 
	 * @return A linked list containing all the workStations.
	 */
	public LinkedList<Workstation> getAllWorkStations(){
		return workStations;
	}
	
	/**
	 * Add's the specified user to the workstation with the specified ID
	 * 
	 * @param user The user that is to be added to the workstation
	 * @param workStation_id The ID of the workstation the user should be added to.
	 */
	public void selectWorkStation(User user, int workStation_id){
		try {
			Workstation selected = selectWorkStationId(workStation_id);
			selected.addCarMechanic(user);
		} catch (Exception e) {
			System.err.println("Could not appoint specified user to specified workstation");
			e.printStackTrace();
		}
	}
	
	/**
	 * Selects the workstation with the specified id from the list of all workstations
	 * 
	 * @param id The ID of the desired workstation
	 * @return The workstation that matches the specified ID
	 * @throws Exception when no workstation with the specified ID exists.
	 */
	public Workstation selectWorkStationId(int id) throws Exception{
		Workstation selected = null;
		for(Workstation w : getAllWorkStations()){
			if(w.getId() == id)
				selected = w;
		}
		if(selected == null)
			throw new Exception("No workstation exists with ID: " + id);
		return selected;
	}
	
	/**
	 * This method creates 3 workstations, specifies their ID's and the respective assembly tasks those workstations can perform.
	 */
	public void createWorkStations(){
		Workstation workStation1 = new Workstation(1, null); // null moet een lijst van alle mogelijke tasks worden (als String)
		Workstation workStation2 = new Workstation(2, null); // null moet een lijst van alle mogelijke tasks worden (als String)
		Workstation workStation3 = new Workstation(3, null); // null moet een lijst van alle mogelijke tasks worden (als String)
		this.workStations.add(workStation1);
		this.workStations.add(workStation2);
		this.workStations.add(workStation3);
	}
}

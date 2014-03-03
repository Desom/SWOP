package Main;
import java.util.LinkedList;


public class Company {
	
	private AssemblyLine assemblyLine = null;
	private final CarModelCatalog catalog;
	private final OrderManager orderManager;
	
	/**
	 * Constructor for the company class.
	 * This constructor is also responsible for the creation of 1 or more assemblyLines.
	 * This constructor is also responsible for the creation of the cat model catalog.
	 */
	public Company(){
		this.assemblyLine = new AssemblyLine();
		this.catalog = new CarModelCatalog();
		this.orderManager = new OrderManager();
		// Is dit niet wat jullie bedoelden met de methode CreateWorld()?
	}
	
	/**
	 * Gives a LinkedList of all the workstations.
	 * 
	 * @param user The user requesting this information
	 * @return Returns the list of all workstations if the specified user is allowed to access this information. Otherwise it returns null
	 */
	public LinkedList<Workstation> getAllWorkStations(User user){
		if(user.canPerform("getAllWorkStations")){
			return assemblyLine.getAllWorkStations(); //moet dit een kopie zijn ivm beveiliging?
		}else{
			return null;
		}
	}
	
	/**
	 * Add's the specified user to the workstation matching the given workStation id if the specified user is allowed to perform this action.
	 * 
	 * @param user The user that wants to be added to the given workstation.
	 * @param workStation_id The id of the workstation the user wants to be added to.
	 */
	public void selectWorkStation(User user, int workStation_id){
		if(user.canPerform("selectWorkStation")){
			assemblyLine.selectWorkStation(user, workStation_id);
		}
	}
	
	/**
	 * Returns the company's car model catalog.
	 * @param user The user requesting the catalog
	 * @return If the user is allowed to request the catalog, return the catalog, else return null;
	 */
	public CarModelCatalog getCatalog(User user){
		if(user.canPerform("getCatalog")){
			return catalog;
		}
		return null;
	}
	
	
	/**
	 * Returns the company's order manager.
	 * @param user The user requesting the order manager
	 * @return If the user is allowed to request the order manager, return the order manager, else return null;
	 */
	public OrderManager getOrderManager(User user){
		if(user.canPerform("getOrderManager")){
			return orderManager;
		}
		return null;
	}

}

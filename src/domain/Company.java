package domain;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.AssemblyLine;
import domain.assembly.DoesNotExistException;
import domain.assembly.Workstation;
import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.order.OrderManager;
import domain.user.CarMechanic;
import domain.user.CustomShopManager;
import domain.user.GarageHolder;
import domain.user.Manager;
import domain.user.User;


public class Company {

	private AssemblyLine assemblyLine = null;
	private final CarModelCatalog catalog;
	private final OrderManager orderManager;
	private final Statistics statistics;

	/**
	 * Constructor for the company class.
	 * This constructor is also responsible for the creation of 1 or more assemblyLines.
	 * This constructor is also responsible for the creation of the car model catalog.
	 * This constructor is also responsible for the creation of the statistics object
	 * @throws InternalFailureException 
	 * @throws CarModelCatalogException 
	 * @throws IOException 
	 */
	public Company() throws InternalFailureException {
		try {
			this.catalog = new CarModelCatalog();
			this.orderManager = new OrderManager(new GregorianCalendar(2014, 1, 1, 12, 0, 0));
			this.statistics = new Statistics();
			this.assemblyLine = new AssemblyLine(orderManager.getProductionSchedule(), this.statistics);
		} catch (IOException | CarModelCatalogException e) {
			throw new InternalFailureException("Failed to initialise Company");
		}
	}

	/**
	 * Gives a LinkedList of all the workstations.
	 * 
	 * @param user The user requesting this information
	 * @return Returns the list of all workstations if the specified user is allowed to access this information. Otherwise it returns null
	 * @throws UserAccessException 
	 */
	public LinkedList<Workstation> getAllWorkstations(){
		return assemblyLine.getAllWorkstations(); //moet dit een kopie zijn ivm beveiliging?
	}

	/**
	 * Add's the specified user to the workstation matching the given workStation id if the specified user is allowed to perform this action.
	 * 
	 * @param user The user that wants to be added to the given workstation.
	 * @param workStation_id The id of the workstation the user wants to be added to.
	 * @throws UserAccessException 
	 * @throws Exception 
	 */
	/*public void selectWorkstation(User user, int workStation_id) throws UserAccessException, InternalFailureException{
		if(user.canPerform("selectWorkStation")){
			assemblyLine.selectWorkstation(user, workStation_id);
		}else{
			throw new UserAccessException(user, "selectWorkStation");
		}
	}*/

	/**
	 * Returns the company's car model catalog.
	 * @return If the user is allowed to request the catalog, return the catalog, else return null;
	 * @throws UserAccessException 
	 */
	public CarModelCatalog getCatalog()  {
		return catalog;
	}


	/**
	 * Returns the company's order manager.
	 * @param user The user requesting the order manager
	 * @return If the user is allowed to request the order manager, return the order manager, else throw UserAccessException;
	 * @throws UserAccessException 
	 */
	public OrderManager getOrderManager(){
		return orderManager;
	}

	/**
	 * Returns the company's assembly line.
	 * @param user The user requesting the aasembly Line
	 * @return If the user is allowed to request the assembly line, return the assembly line, else throw UserAccessException;
	 * @throws UserAccessException
	 */
	public AssemblyLine getAssemblyLine(){
		return this.assemblyLine;
	}


	/**
	 * Get a view of the current statistics. This view has all kinds of getters to get a better overview of the data.
	 * 
	 * @return a view on the current statistics.
	 */
	public StatisticsView viewStatistics(){
		return this.statistics.getView();
	}


	/**
	 * Creates a manager when requested
	 * @param ID The Id that is to be associated with this user
	 * @return The user object that was created
	 */
	public User createManager(int ID){
		return new Manager(ID);
	}
	
	/**
	 * Creates a car mechanic when requested
	 * @param ID The Id that is to be associated with this user
	 * @return The user object that was created
	 */
	public User createCarmechanic(int ID){
		return new CarMechanic(ID);
	}
	
	/**
	 * Creates a garage holder when requested
	 * @param ID The Id that is to be associated with this user
	 * @return The user object that was created
	 */
	public User createGarageHolder(int ID){
		return new GarageHolder(ID);
	}
	
	/**
	 * Creates a custom shop owner when requested
	 * @param ID The Id that is to be associated with this user
	 * @return The user object that was created
	 */
	public User createCustomShopOwner(int ID){
		return new CustomShopManager(ID);
	}


}

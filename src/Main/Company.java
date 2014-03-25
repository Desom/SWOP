package Main;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import Assembly.AssemblyLine;
import Assembly.DoesNotExistException;
import Assembly.Workstation;
import Order.CarModelCatalog;
import Order.CarModelCatalogException;
import Order.OrderManager;
import User.CarMechanic;
import User.GarageHolder;
import User.Manager;
import User.User;
import User.UserAccessException;


public class Company {

	private AssemblyLine assemblyLine = null;
	private CarModelCatalog catalog;
	private OrderManager orderManager;

	/**
	 * Constructor for the company class.
	 * This constructor is also responsible for the creation of 1 or more assemblyLines.
	 * This constructor is also responsible for the creation of the cat model catalog.
	 * @throws InternalFailureException 
	 * @throws CarModelCatalogException 
	 * @throws IOException 
	 */
	public Company() throws InternalFailureException {
		try {
			this.catalog = new CarModelCatalog();
			this.orderManager = new OrderManager(new GregorianCalendar(2014, 1, 1, 12, 0, 0));
			this.assemblyLine = new AssemblyLine(orderManager.getProductionSchedule());
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
	public LinkedList<Workstation> getAllWorkstations(User user) throws UserAccessException{
		if(user.canPerform("getAllWorkstations")){
			return assemblyLine.getAllWorkstations(user); //moet dit een kopie zijn ivm beveiliging?
		}else{
			throw new UserAccessException(user, "getAllWorkstations");
		}
	}

	/**
	 * Add's the specified user to the workstation matching the given workStation id if the specified user is allowed to perform this action.
	 * 
	 * @param user The user that wants to be added to the given workstation.
	 * @param workStation_id The id of the workstation the user wants to be added to.
	 * @throws UserAccessException 
	 * @throws Exception 
	 */
	public void selectWorkstation(User user, int workStation_id) throws UserAccessException, InternalFailureException{
		if(user.canPerform("selectWorkStation")){
			assemblyLine.selectWorkstation(user, workStation_id);
		}else{
			throw new UserAccessException(user, "selectWorkStation");
		}
	}

	/**
	 * Returns the company's car model catalog.
	 * @param user The user requesting the catalog
	 * @return If the user is allowed to request the catalog, return the catalog, else return null;
	 * @throws UserAccessException 
	 */
	public CarModelCatalog getCatalog(User user) throws UserAccessException{
		if(user.canPerform("getCatalog")){
			return catalog;
		}else{
			throw new UserAccessException(user, "getCatalog");
		}
	}


	/**
	 * Returns the company's order manager.
	 * @param user The user requesting the order manager
	 * @return If the user is allowed to request the order manager, return the order manager, else throw UserAccessException;
	 * @throws UserAccessException 
	 */
	public OrderManager getOrderManager(User user) throws UserAccessException{
		if(user.canPerform("getOrderManager")){
			return orderManager;
		}else{
			throw new UserAccessException(user, "getOrderManager");
		}
	}

	/**
	 * Returns the company's assembly line.
	 * @param user The user requesting the aasembly Line
	 * @return If the user is allowed to request the assembly line, return the assembly line, else throw UserAccessException;
	 * @throws UserAccessException
	 */
	public AssemblyLine getAssemblyLine(User user) throws UserAccessException{
		if(user.canPerform("getAssemblyLine")){
			return this.assemblyLine;
		}else{
			throw new UserAccessException(user, "getAssemblyLine");
		}
	}


	/**
	 * Creates a user when requested
	 * @param userName type of the user to create
	 * @param ID The Id that is to be associated with this user
	 * @return The user object that was created
	 * @throws DoesNotExistException
	 */
	public User createUser(String userName, int ID) throws DoesNotExistException{
		switch(userName){
		case "manager":
			return new Manager(ID);
		case "garageholder":
			return new GarageHolder(ID);
		case "carmechanic":
			return new CarMechanic(ID);
		default:
			throw new DoesNotExistException("user type " + userName + " does not exist");
		}
	}

}

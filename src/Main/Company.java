package Main;
import java.io.IOException;
import java.util.LinkedList;

import Assembly.AssemblyLine;
import Assembly.Workstation;
import Order.CarModelCatalog;
import Order.CarModelCatalogException;
import Order.OrderManager;
import User.User;
import User.UserAccessException;


public class Company {
	
	private AssemblyLine assemblyLine = null;
	private final CarModelCatalog catalog;
	private final OrderManager orderManager;
	
	/**
	 * Constructor for the company class.
	 * This constructor is also responsible for the creation of 1 or more assemblyLines.
	 * This constructor is also responsible for the creation of the cat model catalog.
	 * @throws CarModelCatalogException 
	 * @throws IOException 
	 */
	public Company() throws IOException, CarModelCatalogException{ // catalog exception
		this.catalog = new CarModelCatalog();
		this.orderManager = new OrderManager(catalog);
		this.assemblyLine = new AssemblyLine(orderManager.getProductionSchedule());
	}
	
	/**
	 * Gives a LinkedList of all the workstations.
	 * 
	 * @param user The user requesting this information
	 * @return Returns the list of all workstations if the specified user is allowed to access this information. Otherwise it returns null
	 * @throws UserAccessException 
	 */
	public LinkedList<Workstation> getAllWorkStations(User user) throws UserAccessException{
		if(user.canPerform("getAllWorkStations")){
			return assemblyLine.getAllWorkStations(user); //moet dit een kopie zijn ivm beveiliging?
		}else{
			throw new UserAccessException(user, "getAllWorkStations");
		}
	}
	
	/**
	 * Add's the specified user to the workstation matching the given workStation id if the specified user is allowed to perform this action.
	 * 
	 * @param user The user that wants to be added to the given workstation.
	 * @param workStation_id The id of the workstation the user wants to be added to.
	 * @throws Exception 
	 */
	public void selectWorkStation(User user, int workStation_id) throws Exception{
		if(user.canPerform("selectWorkStation")){
			assemblyLine.selectWorkStation(user, workStation_id);
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
	 * @return If the user is allowed to request the order manager, return the order manager, else return null;
	 * @throws UserAccessException 
	 */
	public OrderManager getOrderManager(User user) throws UserAccessException{
		if(user.canPerform("getOrderManager")){
			return orderManager;
		}else{
			throw new UserAccessException(user, "getOrderManager");
		}
	}
	
	
	public AssemblyLine getAssemblyLine(User user) throws UserAccessException{
		if(user.canPerform("getAssemblyLine")){
			return this.assemblyLine;
		}else{
			throw new UserAccessException(user, "getAssemblyLine");
		}
	}
	
	
	/*
	 * ASSEMBLY LINE STATUS EN FUTURE STATUS
	 * 
	 * GEBRUIK OBSERVER KLASSEN? EN ZO JA? HOE?
	 * 
	 * 
	 * 
	 */

}

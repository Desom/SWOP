package domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.Workstation;
import domain.assembly.algorithm.EfficiencySchedulingAlgorithm;
import domain.assembly.algorithm.FIFOSchedulingAlgorithm;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.assembly.algorithm.SpecificationBatchSchedulingAlgorithm;
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
	 * Constructor of Company.
	 * 
	 * This constructor is also responsible for the creation of:
	 * 			the possible scheduling algorithms,
	 * 			the order manager,
	 * 			one or more assembly lines,
	 * 			the car model catalog,
	 *  		the statistics object.
	 * 
	 * @throws InternalFailureException
	 * 		If an exception occurred and the system couldn't recover from it.
	 */
	public Company() {
		try {
			ArrayList<SchedulingAlgorithm> possibleAlgorithms = new ArrayList<SchedulingAlgorithm>();
			possibleAlgorithms.add(new EfficiencySchedulingAlgorithm(new FIFOSchedulingAlgorithm()));
			possibleAlgorithms.add(new EfficiencySchedulingAlgorithm(new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm())));
			GregorianCalendar time = new GregorianCalendar(2014, 0, 1, 6, 0, 0);
			this.catalog = new CarModelCatalog();
			AssemblyLineScheduler scheduler = new AssemblyLineScheduler(time, possibleAlgorithms);
			this.orderManager = new OrderManager(scheduler, time);
			this.statistics = new Statistics(this.orderManager);
			this.assemblyLine = new AssemblyLine(scheduler, null); // TODO
		} catch (IOException e) {
			throw new InternalFailureException("Failed to initialise Company due to an IO exception");
		} catch (CarModelCatalogException e) {
			throw new InternalFailureException("Failed to initialise Company due to an CarModelCatalog exception");
		}
	}

	/**
	 * Returns a LinkedList of all the workstations.
	 * 
	 * @return The list of all workstations.
	 */
	public LinkedList<Workstation> getAllWorkstations(){
		return assemblyLine.getAllWorkstations();
	}

	/**
	 * Returns the company's car model catalog.
	 * 
	 * @return The company's car model catalog.
	 */
	public CarModelCatalog getCatalog()  {
		return catalog;
	}

	/**
	 * Returns the company's order manager.
	 * 
	 * @return The company's order manager. 
	 */
	public OrderManager getOrderManager(){
		return orderManager;
	}

	/**
	 * Returns the company's assembly line.
	 * 
	 * @return The company's assembly line.
	 */
	// TODO moet later weg
	public AssemblyLine getAssemblyLine(){
		return this.assemblyLine;
	}
	
	public ArrayList<AssemblyLine> getAssemblyLines() {
		return null; //TODO
	}

	/**
	 * Get the current statistics. This view has all kinds of getters to get a better overview of the data.
	 * 
	 * @return The current statistics.
	 */
	public Statistics viewStatistics(){
		return this.statistics;
	}

	/**
	 * Creates and returns a manager with the given id.
	 * 
	 * @param id
	 * 		The id that is to be associated with this manager.
	 * @return The Manager object that was created.
	 */
	public User createManager(int id){
		return new Manager(id);
	}
	
	/**
	 * Creates and returns a car mechanic with the given id.
	 * 
	 * @param id
	 * 		The id that is to be associated with this car mechanic.
	 * @return The CarMechanic object that was created.
	 */
	public User createCarmechanic(int id){
		return new CarMechanic(id);
	}
	
	/**
	 * Creates and returns a garage holder with the given id.
	 * 
	 * @param id
	 * 		The id that is to be associated with this garage holder.
	 * @return The GarageHolder object that was created.
	 */
	public User createGarageHolder(int id){
		return new GarageHolder(id);
	}
	
	/**
	 * Creates and returns a custom shop owner with the given id.
	 * 
	 * @param id
	 * 		The id that is to be associated with this custom shop owner.	
	 * @return The CustomShopOwner object that was created.
	 */
	public User createCustomShopOwner(int id){
		return new CustomShopManager(id);
	}
}

package domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineCreator;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.FactoryScheduler;
import domain.assembly.SchedulerCreator;
import domain.assembly.StatusCreator;
import domain.assembly.Workstation;
import domain.assembly.WorkstationTypeCreator;
import domain.assembly.algorithm.AlgorithmCreator;
import domain.configuration.VehicleModelCatalog;
import domain.configuration.VehicleModelCatalogException;
import domain.order.OrderManager;
import domain.user.Mechanic;
import domain.user.CustomShopManager;
import domain.user.GarageHolder;
import domain.user.Manager;
import domain.user.User;

public class Company {

	private ArrayList<AssemblyLine> assemblyLines;
	private final VehicleModelCatalog catalog;
	private final OrderManager orderManager;
	private final Statistics statistics;
	private final FactoryScheduler factoryScheduler;

	/**
	 * TODO docs
	 * 
	 * Constructor of Company.
	 * 
	 * This constructor is also responsible for the creation of:
	 * 			the possible scheduling algorithms,
	 * 			the order manager,
	 * 			one or more assembly lines,
	 * 			the vehicle model catalog,
	 *  		the statistics object.
	 * 
	 * @throws InternalFailureException
	 * 		If an exception occurred and the system couldn't recover from it.
	 */
	public Company() {
		try {
			WorkstationTypeCreator workstationTypeCreator = new WorkstationTypeCreator();
			this.catalog = new VehicleModelCatalog(workstationTypeCreator);
			SchedulerCreator schedulerCreator = new SchedulerCreator(new AlgorithmCreator());
			StatusCreator statusCreator = new StatusCreator();
			AssemblyLineCreator assemblyLineCreator = new AssemblyLineCreator(workstationTypeCreator, schedulerCreator, statusCreator, catalog);

			this.assemblyLines = assemblyLineCreator.create();
			ArrayList<AssemblyLineScheduler> alsList = new ArrayList<AssemblyLineScheduler>();
			
			//TODO goed zo of is er een beter manier?
			for(AssemblyLine assembly : this.assemblyLines){
				alsList.add(assembly.getAssemblyLineScheduler());
			}
			this.factoryScheduler = schedulerCreator.createFactoryScheduler(alsList);
			
			this.orderManager = new OrderManager(this.factoryScheduler);
			this.statistics = new Statistics(this.orderManager);
			
			//TODO kunnen we niet beter de IOException door laten? nu vangen we die en geven we een vage verklaring, terwijl IOException kan uitleggen waaraan het ligt.
		} catch (IOException e) {
			throw new InternalFailureException("Failed to initialise Company due to an IO exception");
		} catch (VehicleModelCatalogException e) {
			throw new InternalFailureException("Failed to initialise Company due to an VehicleModelCatalog exception");
		}
	}

	/**
	 * Returns a LinkedList of all the workstations of the given assembly line.
	 * 
	 * @param assemblyLine
	 * 		The assembly line of which the workstations have to be returned.
	 * @return The list of all workstations.
	 * @throws IllegalArgumentException
	 * 		If the given assembly line is not part of the company.
	 */
	public LinkedList<Workstation> getAllWorkstations(AssemblyLine assemblyLine) throws IllegalArgumentException {
		if (!this.assemblyLines.contains(assemblyLine))
			throw new IllegalArgumentException("The given assembly line is not part of the company.");
		return assemblyLine.getAllWorkstations();
	}

	/**
	 * Returns the company's vehicle model catalog.
	 * 
	 * @return The company's vehicle model catalog.
	 */
	public VehicleModelCatalog getCatalog()  {
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
	 * Returns the assembly lines of this company.
	 * 
	 * @return The assembly lines of this company.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<AssemblyLine> getAssemblyLines() {
		return (ArrayList<AssemblyLine>) this.assemblyLines.clone();
	}

	/**
	 * Returns the factory scheduler of this company.
	 * 
	 * @return The factory scheduler of this company.
	 */
	public FactoryScheduler getFactoryScheduler() {
		return factoryScheduler;
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
	 * Creates and returns a mechanic with the given id.
	 * 
	 * @param id
	 * 		The id that is to be associated with this mechanic.
	 * @return The Mechanic object that was created.
	 */
	public User createMechanic(int id){
		return new Mechanic(id);
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
	 * Creates and returns a custom shop manager with the given id.
	 * 
	 * @param id
	 * 		The id that is to be associated with this custom shop manager.	
	 * @return The CustomShopManager object that was created.
	 */
	public User createCustomShopManager(int id){
		return new CustomShopManager(id);
	}
}

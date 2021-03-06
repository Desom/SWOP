package domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.AssemblyLineCreator;
import domain.assembly.assemblyline.AssemblyLineCreatorInterface;
import domain.assembly.assemblyline.status.StatusCreator;
import domain.assembly.assemblyline.status.StatusCreatorInterface;
import domain.assembly.workstations.Workstation;
import domain.assembly.workstations.WorkstationTypeCreator;
import domain.assembly.workstations.WorkstationTypeCreatorInterface;
import domain.configuration.VehicleCatalog;
import domain.configuration.VehicleCatalogException;
import domain.scheduling.order.OrderManager;
import domain.scheduling.schedulers.AssemblyLineScheduler;
import domain.scheduling.schedulers.FactoryScheduler;
import domain.scheduling.schedulers.SchedulerCreator;
import domain.scheduling.schedulers.SchedulerCreatorInterface;
import domain.scheduling.schedulers.algorithm.AlgorithmCreator;
import domain.user.Mechanic;
import domain.user.CustomShopManager;
import domain.user.GarageHolder;
import domain.user.Manager;
import domain.user.User;

public class Company {

	private ArrayList<AssemblyLine> assemblyLines;
	private final VehicleCatalog catalog;
	private final OrderManager orderManager;
	private final Statistics statistics;
	private final FactoryScheduler factoryScheduler;
	private final WorkstationTypeCreatorInterface workstationTypeCreator;
	private final AssemblyLineCreatorInterface assemblyLineCreator;

	/**
	 * Constructor of Company.
	 * 
	 * This constructor is also responsible for the creation of:
	 * 			the possible scheduling algorithms,
	 * 			the order manager,
	 * 			one or more assembly lines,
	 * 			the vehicle model catalog,
	 *  		the statistics object.
	 * 
	 * @param orderFilePath
	 * 		The path of the text file that contains the orders that need to be loaded in.
	 * @throws IOException 
	 * 		If there is a problem with one of the input files.
	 * @throws InternalFailureException
	 * 		If an exception occurred and the system couldn't recover from it.
	 */
	public Company(String orderFilePath) throws IOException {
		try {
			workstationTypeCreator = new WorkstationTypeCreator();
			this.catalog = new VehicleCatalog(workstationTypeCreator);
			SchedulerCreatorInterface schedulerCreator = new SchedulerCreator(new AlgorithmCreator());
			StatusCreatorInterface statusCreator = new StatusCreator();
			assemblyLineCreator = new AssemblyLineCreator(workstationTypeCreator, schedulerCreator, statusCreator, catalog);

			this.assemblyLines = assemblyLineCreator.create();
			ArrayList<AssemblyLineScheduler> alsList = new ArrayList<AssemblyLineScheduler>();

			for(AssemblyLine assembly : this.assemblyLines){
				alsList.add(assembly.getAssemblyLineScheduler());
			}
			this.factoryScheduler = schedulerCreator.createFactoryScheduler(alsList);

			this.orderManager = new OrderManager(this.factoryScheduler, orderFilePath, catalog);
			this.statistics = new Statistics(this.orderManager);
		}
		catch (VehicleCatalogException e) {
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
	public VehicleCatalog getCatalog()  {
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
	 * Returns the workstationTypeCreator of this company.
	 * 
	 * @return The workstationTypeCreator of this company.
	 */
	public  WorkstationTypeCreatorInterface getworkstationTypeCreator() {
		return workstationTypeCreator;
	}

	/**
	 * Returns the assemblyLineCreator of this company.
	 * 
	 * @return The assemblyLineCreator of this company.
	 */
	public  AssemblyLineCreatorInterface getAssemblyLineCreator() {
		return assemblyLineCreator;
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

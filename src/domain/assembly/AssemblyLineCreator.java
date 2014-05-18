package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import domain.configuration.TaskType;
import domain.configuration.VehicleModelCatalog;

public class AssemblyLineCreator {
	
	private SchedulerCreator schedulerCreator;
	private LinkedList<WorkstationType> workstationTypes;
	
	public AssemblyLineCreator(SchedulerCreator schedulerCreator) {
		createWorkstationTypes();
		this.schedulerCreator = schedulerCreator;
	}
	
	public ArrayList<AssemblyLine> create() {
		ArrayList<AssemblyLine> assemblyLines = new ArrayList<AssemblyLine>();
		
		ArrayList<AssemblyLineStatus> statuses = this.getAssemblyLineStatuses();
		
		GregorianCalendar startTime = new GregorianCalendar(2014, 0, 1, 6, 0, 0);
		
		AssemblyLine assemblyLine1 = new AssemblyLine(this.schedulerCreator.createAssemblyLineScheduler((GregorianCalendar) startTime.clone()), statuses);
		assemblyLine1.addAllWorkstation(this.createCarWorkstations());
		assemblyLines.add(assemblyLine1);
		
		AssemblyLine assemblyLine2 = new AssemblyLine(this.schedulerCreator.createAssemblyLineScheduler((GregorianCalendar) startTime.clone()), statuses);
		assemblyLine2.addAllWorkstation(this.createCarWorkstations());
		assemblyLines.add(assemblyLine2);
		
		AssemblyLine assemblyLine3 = new AssemblyLine(this.schedulerCreator.createAssemblyLineScheduler((GregorianCalendar) startTime.clone()), statuses);
		assemblyLine3.addAllWorkstation(this.createTruckWorkstations());
		assemblyLines.add(assemblyLine3);
		
		return assemblyLines;
	}
	
	
	private ArrayList<Workstation> createCarWorkstations() {
		ArrayList<Workstation> workstations = new ArrayList<Workstation>();
		
		workstations.add(new Workstation("Body Post", getWorkstationType("Body Post")));
		workstations.add(new Workstation("DriveTrain Post", getWorkstationType("DriveTrain Post")));
		workstations.add(new Workstation("Accessories Post", getWorkstationType("Accessories Post")));
		
		return workstations;
	}
	
	
	private ArrayList<Workstation> createTruckWorkstations() {
		ArrayList<Workstation> workstations = new ArrayList<Workstation>();
		
		workstations.add(new Workstation("Body Post", getWorkstationType("Body Post")));
		workstations.add(new Workstation("Cargo Post", getWorkstationType("Cargo Post")));
		workstations.add(new Workstation("DriveTrain Post", getWorkstationType("DriveTrain Post")));
		workstations.add(new Workstation("Accessories Post", getWorkstationType("Accessories Post")));
		
		return workstations;
	}
	
	
	
	private void createWorkstationTypes(){
		workstationTypes = new LinkedList<WorkstationType>();
		LinkedList<TaskType> bodyPost = new LinkedList<TaskType>();
		bodyPost.add(VehicleModelCatalog.taskTypeCreator.Body);
		bodyPost.add(VehicleModelCatalog.taskTypeCreator.Color);
		workstationTypes.add(new WorkstationType("Body Post", bodyPost));
		
		LinkedList<TaskType> driveTrainPost = new LinkedList<TaskType>();
		driveTrainPost.add(VehicleModelCatalog.taskTypeCreator.Engine);
		driveTrainPost.add(VehicleModelCatalog.taskTypeCreator.Gearbox);
		workstationTypes.add(new WorkstationType("DriveTrain Post", driveTrainPost));
		
		LinkedList<TaskType> accessoriesPost = new LinkedList<TaskType>();
		accessoriesPost.add(VehicleModelCatalog.taskTypeCreator.Seats);
		accessoriesPost.add(VehicleModelCatalog.taskTypeCreator.Airco);
		accessoriesPost.add(VehicleModelCatalog.taskTypeCreator.Wheels);
		accessoriesPost.add(VehicleModelCatalog.taskTypeCreator.Spoiler);
		workstationTypes.add(new WorkstationType("Accessories Post", accessoriesPost));
		
		LinkedList<TaskType> cargoPost = new LinkedList<TaskType>(); 
		cargoPost.add(VehicleModelCatalog.taskTypeCreator.ToolStorage);
		cargoPost.add(VehicleModelCatalog.taskTypeCreator.CargoProtection);
		workstationTypes.add(new WorkstationType("Cargo Post", cargoPost));
	}
	
	private ArrayList<AssemblyLineStatus> getAssemblyLineStatuses() {
		ArrayList<AssemblyLineStatus> statuses = new ArrayList<AssemblyLineStatus>();
		statuses.add(new OperationalStatus());
		statuses.add(new MaintenanceStatus());
		statuses.add(new BrokenStatus());
		return statuses;
	}
	
	/**
	 * Return all WorkstationTypes.
	 * 
	 * @return a LinkedList of containing all WorkstationTypes.
	 */
	public LinkedList<WorkstationType> getAllWorkstationTypes(){
		return workstationTypes;
	}
	
	/**
	 * Returns the workstationtype that has the specified name
	 * 
	 * @param name the name of the desired workstationType
	 * @return the requested workstationtype, or null if it does not exist
	 */
	public WorkstationType getWorkstationType(String name){
		for (WorkstationType workstationType : workstationTypes){
			if(workstationType.getName().equals(name)){
				return workstationType;
			}
		}
		return null;
	}
}

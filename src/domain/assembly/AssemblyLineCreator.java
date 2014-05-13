package domain.assembly;

import java.util.ArrayList;
import java.util.LinkedList;

import domain.configuration.CarModelCatalog;
import domain.configuration.OptionType;

public class AssemblyLineCreator {
	
	private AssemblyLineScheduler scheduler;
	private LinkedList<WorkstationType> workstationTypes;
	
	public AssemblyLineCreator(AssemblyLineScheduler scheduler) {
		this.scheduler = scheduler;
		createWorkstationTypes();
	}
	
	public ArrayList<AssemblyLine> create() {
		ArrayList<AssemblyLine> assemblyLines = new ArrayList<AssemblyLine>();
		
		AssemblyLine assemblyLine1 = new AssemblyLine(this.scheduler);
		assemblyLine1.addAllWorkstation(this.createCarWorkstations());
		assemblyLines.add(assemblyLine1);
		
		AssemblyLine assemblyLine2 = new AssemblyLine(this.scheduler);
		assemblyLine2.addAllWorkstation(this.createCarWorkstations());
		assemblyLines.add(assemblyLine2);
		
		AssemblyLine assemblyLine3 = new AssemblyLine(this.scheduler);
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
		LinkedList<OptionType> bodyPost = new LinkedList<OptionType>();
		bodyPost.add(CarModelCatalog.optionTypeCreator.getOptionType("Body"));
		bodyPost.add(CarModelCatalog.optionTypeCreator.getOptionType("Color"));
		workstationTypes.add(new WorkstationType("Body Post", bodyPost));
		
		LinkedList<OptionType> driveTrainPost = new LinkedList<OptionType>();
		driveTrainPost.add(CarModelCatalog.optionTypeCreator.getOptionType("Engine"));
		driveTrainPost.add(CarModelCatalog.optionTypeCreator.getOptionType("Gearbox"));
		workstationTypes.add(new WorkstationType("DriveTrain Post", driveTrainPost));
		
		LinkedList<OptionType> accessoriesPost = new LinkedList<OptionType>();
		accessoriesPost.add(CarModelCatalog.optionTypeCreator.getOptionType("Seats"));
		accessoriesPost.add(CarModelCatalog.optionTypeCreator.getOptionType("Airco"));
		accessoriesPost.add(CarModelCatalog.optionTypeCreator.getOptionType("Wheels"));
		accessoriesPost.add(CarModelCatalog.optionTypeCreator.getOptionType("Spoiler"));
		workstationTypes.add(new WorkstationType("Accessories Post", accessoriesPost));
		
		LinkedList<OptionType> cargoPost = new LinkedList<OptionType>(); 
		// TODO Deze 2 optionTypes bestaan nog niet
		cargoPost.add(CarModelCatalog.optionTypeCreator.getOptionType("ToolStorage"));
		cargoPost.add(CarModelCatalog.optionTypeCreator.getOptionType("CargoProtection"));
		workstationTypes.add(new WorkstationType("Cargo Post", cargoPost));
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
			if(workstationType.getName() == name){
				return workstationType;
			}
		}
		return null;
	}
}

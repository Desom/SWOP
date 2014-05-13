package domain.assembly;

import java.util.ArrayList;
import java.util.LinkedList;

import domain.configuration.CarModelCatalog;
import domain.configuration.OptionType;

public class AssemblyLineCreator {
	
	private Scheduler scheduler;
	private LinkedList<WorkstationType> workstationTypes;
	
	public AssemblyLineCreator(Scheduler scheduler) {
		this.scheduler = scheduler;
		createWorkstationTypes();
	}
	
	public ArrayList<AssemblyLine> create() {
		ArrayList<AssemblyLine> assemblyLines = new ArrayList<AssemblyLine>();
		
		AssemblyLine assemblyLine1 = new AssemblyLine(this.scheduler, this.createCarWorkstations());
		assemblyLines.add(assemblyLine1);
		
		AssemblyLine assemblyLine2 = new AssemblyLine(this.scheduler, this.createCarWorkstations());
		assemblyLines.add(assemblyLine2);
		
		AssemblyLine assemblyLine3 = new AssemblyLine(this.scheduler, this.createTruckWorkstations());
		assemblyLines.add(assemblyLine3);
		
		return assemblyLines;
	}
	
	private ArrayList<Workstation> createCarWorkstations() {
		ArrayList<Workstation> workstations = new ArrayList<Workstation>();
		
		ArrayList<OptionType> taskTypes1 = new ArrayList<OptionType>();
		taskTypes1.add(OptionType.Body);
		taskTypes1.add(OptionType.Color);
		workstations.add(new Workstation("Body Post", taskTypes1));

		ArrayList<OptionType> taskTypes2 = new ArrayList<OptionType>();
		taskTypes2.add(OptionType.Engine);
		taskTypes2.add(OptionType.Gearbox);
		workstations.add(new Workstation("DriveTrain Post", taskTypes2));

		ArrayList<OptionType> taskTypes3 = new ArrayList<OptionType>();
		taskTypes3.add(OptionType.Seats);
		taskTypes3.add(OptionType.Airco);
		taskTypes3.add(OptionType.Wheels);
		taskTypes3.add(OptionType.Spoiler);
		workstations.add(new Workstation("Accessories Post", taskTypes3));
		
		return workstations;
	}
	
	private ArrayList<Workstation> createTruckWorkstations() {
		ArrayList<Workstation> workstations = new ArrayList<Workstation>();
		
		ArrayList<OptionType> taskTypes1 = new ArrayList<OptionType>();
		taskTypes1.add(OptionType.Body);
		taskTypes1.add(OptionType.Color);
		workstations.add(Workstation("Body Post", taskTypes1));
		
		ArrayList<OptionType> taskTypes2 = new ArrayList<OptionType>();
		taskTypes2.add(OptionType.ToolStorage);
		taskTypes2.add(OptionType.CargoProtection);
		workstations.add(new Workstation("Cargo Post", taskTypes2));

		ArrayList<OptionType> taskTypes3 = new ArrayList<OptionType>();
		taskTypes3.add(OptionType.Engine);
		taskTypes3.add(OptionType.Gearbox);
		workstations.add(new Workstation("DriveTrain Post", taskTypes3));
		
		ArrayList<OptionType> taskTypes4 = new ArrayList<OptionType>();
		taskTypes4.add(OptionType.Seats);
		taskTypes4.add(OptionType.Airco);
		taskTypes4.add(OptionType.Wheels);
		taskTypes4.add(OptionType.Spoiler);
		workstations.add(new Workstation("Accessories Post", taskTypes4));
		
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
		
		LinkedList<OptionType> cargoPost = new LinkedList<OptionType>(); // TODO Deze 2 optionTypes bestaan nog niet
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
}

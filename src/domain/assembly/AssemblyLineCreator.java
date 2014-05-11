package domain.assembly;

import java.util.ArrayList;

import domain.configuration.OptionType;

public class AssemblyLineCreator {
	
	private Scheduler scheduler;
	
	public AssemblyLineCreator(Scheduler scheduler) {
		this.scheduler = scheduler;
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
}

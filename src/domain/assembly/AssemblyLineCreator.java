package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import domain.configuration.VehicleModel;
import domain.configuration.VehicleModelCatalog;

public class AssemblyLineCreator {
	
	private final SchedulerCreator schedulerCreator;
	private final VehicleModelCatalog catalog;
	private final WorkstationTypeCreator workstationCreator;
	
	public AssemblyLineCreator(WorkstationTypeCreator workstationCreator, SchedulerCreator schedulerCreator, VehicleModelCatalog catalog) {
		this.schedulerCreator = schedulerCreator;
		this.catalog = catalog;
		this.workstationCreator = workstationCreator;
	}
	
	public ArrayList<AssemblyLine> create() {
		ArrayList<AssemblyLine> assemblyLines = new ArrayList<AssemblyLine>();
		
		ArrayList<AssemblyLineStatus> statuses = this.getAssemblyLineStatuses();
		
		GregorianCalendar startTime = new GregorianCalendar(2014, 0, 1, 6, 0, 0);
		
		ArrayList<VehicleModel> models = new ArrayList<VehicleModel>();
		for(VehicleModel model : this.catalog.getAllModels()){
			if(model.getName().equals("Model A") || model.getName().equals("Model B")){
				models.add(model);
			}
		}
		AssemblyLine assemblyLine1 = new AssemblyLine(this.schedulerCreator.createAssemblyLineScheduler((GregorianCalendar) startTime.clone()), statuses, models);
		assemblyLine1.addAllWorkstation(this.createCarWorkstations());
		assemblyLines.add(assemblyLine1);

		for(VehicleModel model : this.catalog.getAllModels()){
			if(model.getName().equals("Model C")){
				models.add(model);
			}
		}
		AssemblyLine assemblyLine2 = new AssemblyLine(this.schedulerCreator.createAssemblyLineScheduler((GregorianCalendar) startTime.clone()), statuses, models);
		assemblyLine2.addAllWorkstation(this.createCarWorkstations());
		assemblyLines.add(assemblyLine2);
		

		for(VehicleModel model : this.catalog.getAllModels()){
			if(model.getName().equals("Model X") || model.getName().equals("Model Y")){
				models.add(model);
			}
		}
		AssemblyLine assemblyLine3 = new AssemblyLine(this.schedulerCreator.createAssemblyLineScheduler((GregorianCalendar) startTime.clone()), statuses, models);
		assemblyLine3.addAllWorkstation(this.createTruckWorkstations());
		assemblyLines.add(assemblyLine3);
		
		return assemblyLines;
	}
	
	
	private ArrayList<Workstation> createCarWorkstations() {
		ArrayList<Workstation> workstations = new ArrayList<Workstation>();
		
		workstations.add(new Workstation("Body Post", workstationCreator.getWorkstationType("Body Post")));
		workstations.add(new Workstation("DriveTrain Post", workstationCreator.getWorkstationType("DriveTrain Post")));
		workstations.add(new Workstation("Accessories Post", workstationCreator.getWorkstationType("Accessories Post")));
		
		return workstations;
	}
	
	
	private ArrayList<Workstation> createTruckWorkstations() {
		ArrayList<Workstation> workstations = new ArrayList<Workstation>();
		
		workstations.add(new Workstation("Body Post", workstationCreator.getWorkstationType("Body Post")));
		workstations.add(new Workstation("Cargo Post", workstationCreator.getWorkstationType("Cargo Post")));
		workstations.add(new Workstation("DriveTrain Post", workstationCreator.getWorkstationType("DriveTrain Post")));
		workstations.add(new Workstation("Accessories Post", workstationCreator.getWorkstationType("Accessories Post")));
		workstations.add(new Workstation("Certification Post", workstationCreator.getWorkstationType("Certification Post")));
		
		return workstations;
	}
	
	
	
	
	
	private ArrayList<AssemblyLineStatus> getAssemblyLineStatuses() {
		ArrayList<AssemblyLineStatus> statuses = new ArrayList<AssemblyLineStatus>();
		statuses.add(new OperationalStatus());
		statuses.add(new MaintenanceStatus());
		statuses.add(new BrokenStatus());
		return statuses;
	}
	
	
}

package domain.assembly.assemblyline;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.assembly.assemblyline.status.StatusCreatorInterface;
import domain.assembly.workstations.Workstation;
import domain.assembly.workstations.WorkstationTypeCreatorInterface;
import domain.configuration.VehicleCatalog;
import domain.configuration.models.VehicleModel;
import domain.scheduling.schedulers.SchedulerCreatorInterface;

public class AssemblyLineCreator implements AssemblyLineCreatorInterface {
	
	private final SchedulerCreatorInterface schedulerCreator;
	private final VehicleCatalog catalog;
	private final StatusCreatorInterface statusCreator;
	private final WorkstationTypeCreatorInterface workstationCreator;
	
	public AssemblyLineCreator(WorkstationTypeCreatorInterface workstationCreator, SchedulerCreatorInterface schedulerCreator, StatusCreatorInterface statusCreator, VehicleCatalog catalog) {
		this.schedulerCreator = schedulerCreator;
		this.catalog = catalog;
		this.statusCreator = statusCreator;
		this.workstationCreator = workstationCreator;
	}
	
	/* (non-Javadoc)
	 * @see domain.assembly.AssemblyLineCreatorInterface#create()
	 */
	@Override
	public ArrayList<AssemblyLine> create() {
		ArrayList<AssemblyLine> assemblyLines = new ArrayList<AssemblyLine>();
		
		GregorianCalendar startTime = new GregorianCalendar(2014, 0, 1, 6, 0, 0);
		
		ArrayList<VehicleModel> models = new ArrayList<VehicleModel>();
		for(VehicleModel model : this.catalog.getAllModels()){
			if(model.getName().equals("Model A") || model.getName().equals("Model B")){
				models.add(model);
			}
		}
		AssemblyLine assemblyLine1 = new AssemblyLine(this.schedulerCreator.createAssemblyLineScheduler((GregorianCalendar) startTime.clone()), this.statusCreator.getOperationalStatus(), models);
		assemblyLine1.addAllWorkstation(this.createCarWorkstations());
		assemblyLines.add(assemblyLine1);

		for(VehicleModel model : this.catalog.getAllModels()){
			if(model.getName().equals("Model C")){
				models.add(model);
			}
		}
		AssemblyLine assemblyLine2 = new AssemblyLine(this.schedulerCreator.createAssemblyLineScheduler((GregorianCalendar) startTime.clone()), this.statusCreator.getOperationalStatus(), models);
		assemblyLine2.addAllWorkstation(this.createCarWorkstations());
		assemblyLines.add(assemblyLine2);
		

		for(VehicleModel model : this.catalog.getAllModels()){
			if(model.getName().equals("Model X") || model.getName().equals("Model Y")){
				models.add(model);
			}
		}
		AssemblyLine assemblyLine3 = new AssemblyLine(this.schedulerCreator.createAssemblyLineScheduler((GregorianCalendar) startTime.clone()), this.statusCreator.getOperationalStatus(), models);
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
	
}
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
	
	/**
	 * Constructor of AssemblyLineCreator.
	 * 
	 * @param workstationCreator
	 * 		The creator of Workstation objects.
	 * @param schedulerCreator
	 * 		The creator of Scheduler objects.
	 * @param statusCreator
	 * 		The creator of Status objects.
	 * @param catalog
	 * 		The vehicle catalog.
	 */
	public AssemblyLineCreator(WorkstationTypeCreatorInterface workstationCreator, SchedulerCreatorInterface schedulerCreator, StatusCreatorInterface statusCreator, VehicleCatalog catalog) {
		this.schedulerCreator = schedulerCreator;
		this.catalog = catalog;
		this.statusCreator = statusCreator;
		this.workstationCreator = workstationCreator;
	}
	
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
		AssemblyLine assemblyLine1 = new AssemblyLine("Assembly line 1", this.createCarWorkstations(), this.schedulerCreator.createAssemblyLineScheduler((GregorianCalendar) startTime.clone()), this.statusCreator.getOperationalStatus(), models);
		assemblyLines.add(assemblyLine1);

		for(VehicleModel model : this.catalog.getAllModels()){
			if(model.getName().equals("Model C")){
				models.add(model);
			}
		}
		AssemblyLine assemblyLine2 = new AssemblyLine("Assembly line 2", this.createCarWorkstations(), this.schedulerCreator.createAssemblyLineScheduler((GregorianCalendar) startTime.clone()), this.statusCreator.getOperationalStatus(), models);
		assemblyLines.add(assemblyLine2);
		

		for(VehicleModel model : this.catalog.getAllModels()){
			if(model.getName().equals("Model X") || model.getName().equals("Model Y")){
				models.add(model);
			}
		}
		AssemblyLine assemblyLine3 = new AssemblyLine("Assembly line 3", this.createTruckWorkstations(), this.schedulerCreator.createAssemblyLineScheduler((GregorianCalendar) startTime.clone()), this.statusCreator.getOperationalStatus(), models);
		assemblyLines.add(assemblyLine3);
		
		return assemblyLines;
	}
	
	/**
	 * Creates all workstations for cars.
	 * 
	 * @return All workstations for cars.
	 */
	private ArrayList<Workstation> createCarWorkstations() {
		ArrayList<Workstation> workstations = new ArrayList<Workstation>();
		
		workstations.add(new Workstation("Body Post", workstationCreator.getWorkstationType("Body Post")));
		workstations.add(new Workstation("DriveTrain Post", workstationCreator.getWorkstationType("DriveTrain Post")));
		workstations.add(new Workstation("Accessories Post", workstationCreator.getWorkstationType("Accessories Post")));
		
		return workstations;
	}
	
	/**
	 * Creates all workstations for trucks.
	 * 
	 * @return All workstations for trucks.
	 */
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

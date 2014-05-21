package domain.assembly;

import java.util.LinkedList;

import domain.configuration.TaskType;
import domain.configuration.VehicleModelCatalog;

public class WorkstationTypeCreator implements WorkstationTypeCreatorInterface {
	
	private LinkedList<WorkstationType> workstationTypes;
	
	public WorkstationTypeCreator(){
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
		
		LinkedList<TaskType> CertificationPost = new LinkedList<TaskType>(); 
		cargoPost.add(VehicleModelCatalog.taskTypeCreator.Certification);
		workstationTypes.add(new WorkstationType("Certification Post", CertificationPost));
	}
	
	/* (non-Javadoc)
	 * @see domain.assembly.WorkstationTypeCreatorInterface#getAllWorkstationTypes()
	 */
	@Override
	public LinkedList<WorkstationType> getAllWorkstationTypes(){
		return workstationTypes;
	}
	
	/* (non-Javadoc)
	 * @see domain.assembly.WorkstationTypeCreatorInterface#getWorkstationType(java.lang.String)
	 */
	@Override
	public WorkstationType getWorkstationType(String name){
		for (WorkstationType workstationType : workstationTypes){
			if(workstationType.getName().equals(name)){
				return workstationType;
			}
		}
		return null;
	}
}

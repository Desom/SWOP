package domain.assembly.workstations;

import java.util.LinkedList;

import domain.configuration.VehicleCatalog;
import domain.configuration.Taskables.TaskType;

public class WorkstationTypeCreator implements WorkstationTypeCreatorInterface {
	
	private LinkedList<WorkstationType> workstationTypes;
	
	public WorkstationTypeCreator(){
		workstationTypes = new LinkedList<WorkstationType>();
		LinkedList<TaskType> bodyPost = new LinkedList<TaskType>();
		bodyPost.add(VehicleCatalog.taskTypeCreator.Body);
		bodyPost.add(VehicleCatalog.taskTypeCreator.Color);
		workstationTypes.add(new WorkstationType("Body Post", bodyPost));
		
		LinkedList<TaskType> driveTrainPost = new LinkedList<TaskType>();
		driveTrainPost.add(VehicleCatalog.taskTypeCreator.Engine);
		driveTrainPost.add(VehicleCatalog.taskTypeCreator.Gearbox);
		workstationTypes.add(new WorkstationType("DriveTrain Post", driveTrainPost));
		
		LinkedList<TaskType> accessoriesPost = new LinkedList<TaskType>();
		accessoriesPost.add(VehicleCatalog.taskTypeCreator.Seats);
		accessoriesPost.add(VehicleCatalog.taskTypeCreator.Airco);
		accessoriesPost.add(VehicleCatalog.taskTypeCreator.Wheels);
		accessoriesPost.add(VehicleCatalog.taskTypeCreator.Spoiler);
		workstationTypes.add(new WorkstationType("Accessories Post", accessoriesPost));
		
		LinkedList<TaskType> cargoPost = new LinkedList<TaskType>(); 
		cargoPost.add(VehicleCatalog.taskTypeCreator.ToolStorage);
		cargoPost.add(VehicleCatalog.taskTypeCreator.CargoProtection);
		workstationTypes.add(new WorkstationType("Cargo Post", cargoPost));
		
		LinkedList<TaskType> CertificationPost = new LinkedList<TaskType>(); 
		cargoPost.add(VehicleCatalog.taskTypeCreator.Certification);
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

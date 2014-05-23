package domain.assembly.assemblyline;

import java.util.ArrayList;
import java.util.LinkedList;

import domain.assembly.workstations.AssemblyTask;
import domain.assembly.workstations.Workstation;
import domain.assembly.workstations.WorkstationType;
import domain.configuration.TaskType;

public class AssemblyStatusView {

	private String header;
	private ArrayList<Workstation> workstations = new ArrayList<Workstation>();
	
	/**
	 * Constructor of AssemblyStatusView.
	 * @param workstations
	 * 		The workstations to be added to the view.
	 * @param header
	 * 		The header of this view.
	 */
	public AssemblyStatusView(ArrayList<Workstation> workstations, String header){
		this.header = header;
		this.workstations = workstations;
	}

	/**
	 * Returns the types of the workstations.
	 * 
	 * @return the types of the workstations.
	 */
	public ArrayList<WorkstationType> getAllWorkstationTypes(){
		ArrayList<WorkstationType> types = new ArrayList<WorkstationType>();
		for(Workstation workstation : workstations){
			types.add(workstation.getWorkstationType());
		}
		return types;
	}
	
	/**
	 * Returns the order at the workstation indicated with the id.
	 * 
	 * @param workstationId
	 * 		The id that indicates the workstation.
	 * @return The order at the indicated workstation. If no order is present, it will return -1.
	 * @throws DoesNotExistException
	 * 		If the workstation with the associated id does not exist.
	 */
	public int getOrderIdOf(WorkstationType workstationType) throws DoesNotExistException{
		if(getWorkstation(workstationType) != null && getWorkstation(workstationType).getVehicleAssemblyProcess() != null){
			return getWorkstation(workstationType).getVehicleAssemblyProcess().getOrder().getOrderID();
		}
		return -1;
	}

	/**
	 * Returns all tasks at the workstation associated with the given id.
	 * 
	 * @param workstationId
	 * 		The id of the workstation which has to desired tasks.
	 * @return All tasks at the workstation associated with the given id.
	 * @throws DoesNotExistException
	 * 		If the given id isn't associated with a workstation.
	 */
	public LinkedList<TaskType> getAllTasksAt(WorkstationType type) throws DoesNotExistException{
		LinkedList<TaskType> tasks = new LinkedList<TaskType>();
		Workstation workstation = getWorkstation(type);
		for(AssemblyTask t : workstation.getAllTasks()){
			tasks.add(t.getType());
		}
		return tasks;
	}

	/**
	 * Returns true if the task corresponding to the option type is completed in the indicated workstation, otherwise false.
	 * 
	 * @param optionType
	 * 		The type of option.
	 * @param workstationId
	 * 		The id that indicates the workstation.
	 * @return True if the task corresponding to the option type is completed in the indicated workstation, otherwise false.
	 * @throws DoesNotExistException
	 * 		If the workstation with the given id doesn't exist.
	 * 		If the option type has no corresponding task in the workstation. 
	 */
	public boolean taskIsDoneAt(TaskType taskType, WorkstationType type) throws DoesNotExistException{
		Workstation workstation = getWorkstation(type);
		for(AssemblyTask task : workstation.getAllTasks()){
			if(task.getType() == taskType){
				return task.isCompleted();
			}
		}
		throw new DoesNotExistException("The workstation with ID " + type + " does not have a task with type " + taskType);
	}

	/**
	 * Returns the header of this view.
	 * 
	 * @return The header of this view.
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Returns the workstation at the specified position (starting at 0) in the assemblyLine
	 * @param position
	 * 		The index of the workstation in the assemblyline.
	 * @return The workstation associated with the given id.
	 * @throws DoesNotExistException
	 * 		If the desired workstation does not exist.
	 */
	@SuppressWarnings("unused")
	private Workstation getWorkstation(int position) throws DoesNotExistException{
		if(this.workstations.size() <= position){
			throw new DoesNotExistException("There is no workstation at position: " + position);
		}
		Workstation found = this.workstations.get(position);
		return found;
	}
	
	/**
	 * Returns the workstation of the specified type
	 * @param workstationType
	 * 		The type of the workstation in the assemblyline.
	 * @return The workstation associated with the given type.
	 * @throws DoesNotExistException
	 * 		If the desired workstation does not exist.
	 */
	private Workstation getWorkstation(WorkstationType workstationType) throws DoesNotExistException{
		for(Workstation w : workstations){
			if(w.getWorkstationType() == workstationType){
				return w;
			}
		}
		throw new DoesNotExistException("No workstation with type " + workstationType.toString() + " exists on this assemblyLine");
	}
}

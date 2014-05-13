package domain.assembly;

import java.util.ArrayList;
import java.util.LinkedList;

import domain.configuration.OptionType;

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

	/*
	 * Returns all names of the workstations.
	 * 
	 * @return All names of the workstations.
	 */
	public ArrayList<Integer> getAllWorkstationTypeNames(){
		ArrayList<Integer> names = new ArrayList<Integer>();
		for(Workstation workstation : workstations){
			ids.add(workstation.getId());
		}
		return ids;
	}
	
	/**
	 * Returns the car order at the workstation indicated with the id.
	 * 
	 * @param workstationId
	 * 		The id that indicates the workstation.
	 * @return The car order at the indicated workstation. If no car order is present, it will return -1.
	 * @throws DoesNotExistException
	 * 		If the workstation with the associated id does not exist.
	 */
	public int getCarOrderIdAt(int workstationId) throws DoesNotExistException{
		if(getWorkstation(workstationId) != null && getWorkstation(workstationId).getCarAssemblyProcess() != null){
			return getWorkstation(workstationId).getCarAssemblyProcess().getOrder().getCarOrderID();
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
	public LinkedList<OptionType> getAllTasksAt(int workstationId) throws DoesNotExistException{
		LinkedList<OptionType> tasks = new LinkedList<OptionType>();
		Workstation workstation = getWorkstation(workstationId);
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
	public boolean taskIsDoneAt(OptionType optionType, int workstationId) throws DoesNotExistException{
		Workstation workstation = getWorkstation(workstationId);
		for(AssemblyTask task : workstation.getAllTasks()){
			if(task.getType() == optionType){
				return task.isCompleted();
			}
		}
		throw new DoesNotExistException("The workstation with ID " + workstationId + " does not have a task with type " + optionType);
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
	 * TODO
	 * @param position
	 * 		The index of the workstation in the assemblyline.
	 * @return The workstation associated with the given id.
	 * @throws DoesNotExistException
	 * 		If the desired workstation does not exist.
	 */
	private Workstation getWorkstation(int position) throws DoesNotExistException{
		if(this.workstations.size() <= position){
			throw new DoesNotExistException("There is no workstation at position: " + position);
		}
		Workstation found = this.workstations.get(position);
		return found;
	}
}

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

	/**
	 * Returns all id's of the workstations.
	 * 
	 * @return All id's of the workstations.
	 */
	public ArrayList<Integer> getAllWorkstationIds(){
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for(Workstation workstation : workstations){
			ids.add(workstation.getId());
		}
		return ids;
	}
	
	/**
	 * TODO + is -1 terug sturen goed?
	 * @param workstationId
	 * @return
	 * @throws DoesNotExistException
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
	 * 		If the task type
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

	public String getHeader() {
		return header;
	}

	private Workstation getWorkstation(int ID) throws DoesNotExistException{
		Workstation found = null;
		for(Workstation w : workstations){
			if(w.getId() == ID){
				found = w;
			}
		}
		if(found == null){
			throw new DoesNotExistException("There is no workstation with ID: " + ID);
		}
		return found;
	}
}

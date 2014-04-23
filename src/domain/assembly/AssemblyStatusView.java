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
	 * @return all id's of the workstations
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

	public LinkedList<OptionType> getAllTasksAt(int workstationId) throws DoesNotExistException{
		LinkedList<OptionType> tasks = new LinkedList<OptionType>();
		Workstation w = getWorkstation(workstationId);
		for(AssemblyTask t : w.getAllTasks()){
			//tasks.add("Type: " + t.getType() + "  -  Completed: " + t.isCompleted());
			tasks.add(t.getType());
		}
		return tasks;
	}

	public boolean taskIsDoneAt(OptionType taskType,int workstationId) throws DoesNotExistException{
		Workstation w = getWorkstation(workstationId);
		for(AssemblyTask t : w.getAllTasks()){
			if(t.getType() == taskType){
				return t.isCompleted();
			}
		}
		throw new DoesNotExistException("The workstation with ID " + workstationId + " does not have a task with type " + taskType);
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

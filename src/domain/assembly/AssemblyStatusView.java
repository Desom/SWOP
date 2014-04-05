package domain.assembly;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class AssemblyStatusView {

	private String header;
	//private returnType status;
	private LinkedHashMap<Workstation, CarAssemblyProcess> workstations = new LinkedHashMap<Workstation, CarAssemblyProcess>();
	
	public AssemblyStatusView(LinkedHashMap<Workstation, CarAssemblyProcess> workstations, String header){
		this.header = header;
		//this.status = status;
		this.workstations = workstations;
	}

	/**
	 * 
	 * @return an array of integers representing the ID's of the workstations
	 */
	public int[] getAllWorkstationIds(){
		int[] IDs = new int[workstations.size()];
		int i = 0;
		for(Workstation w : workstations.keySet()){
			IDs[i] = w.getId();
			i++;
		}
		return IDs;
	}
	/**
	 * TODO + is -1 terug sturen goed?
	 * @param workstationId
	 * @return
	 * @throws DoesNotExistException
	 */
	public int getCarOrderIdAt(int workstationId) throws DoesNotExistException{
		if(workstations.get(getWorkstation(workstationId)) != null){
			return workstations.get(getWorkstation(workstationId)).getCar().getOrder().getCarOrderID();
		}
		return -1;
	}

	public LinkedList<String> getAllTasksAt(int workstationId) throws DoesNotExistException{
		LinkedList<String> tasks = new LinkedList<String>();
		Workstation w = getWorkstation(workstationId);
		for(AssemblyTask t : w.getAllTasks()){
			//tasks.add("Type: " + t.getType() + "  -  Completed: " + t.isCompleted());
			tasks.add(t.getType().toString());
		}
		return tasks;
	}

	public boolean taskIsDoneAt(String task,int workstationId) throws DoesNotExistException{
		Workstation w = getWorkstation(workstationId);
		for(AssemblyTask t : w.getAllTasks()){
			if(t.getType().toString().compareToIgnoreCase(task) == 0){
				return t.isCompleted();
			}
		}
		throw new DoesNotExistException("The workstation with ID " + workstationId + " does not have a task with type " + task);
	}

	public String getHeader() {
		return header;
	}

	private Workstation getWorkstation(int ID) throws DoesNotExistException{
		Workstation found = null;
		for(Workstation w : workstations.keySet()){
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

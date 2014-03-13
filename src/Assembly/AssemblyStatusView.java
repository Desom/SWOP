package Assembly;

import java.util.ArrayList;
import java.util.LinkedList;

import User.User;
import User.UserAccessException;

public class AssemblyStatusView {

	private String header;
	private final User user;
	//private returnType status;
	private ArrayList<Workstation> workstations = new ArrayList<Workstation>();
	public AssemblyStatusView(User user, ArrayList<Workstation> stations, String header){
		this.header = header;
		//this.status = status;
		this.workstations = stations;
		this.user = user;
	}

	/**
	 * 
	 * @return an array of integers representing the ID's of the workstations
	 */
	public int[] getAllWorkstationIds(){
		int[] IDs = new int[workstations.size()];
		int i = 0;
		for(Workstation w : workstations){
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
		return getWorkstation(workstationId).getCurrentCar().getCar().getOrder().getCarOrderID();
	}

	public LinkedList<String> getAllTasksAt(int workstationId) throws UserAccessException, DoesNotExistException{
		LinkedList<String> tasks = new LinkedList<String>();
		Workstation w = getWorkstation(workstationId);
		for(AssemblyTask t : w.getAllTasks(user)){
			//tasks.add("Type: " + t.getType() + "  -  Completed: " + t.isCompleted());
			tasks.add(t.getType());
		}
		return tasks;
	}

	public boolean taskIsDoneAt(String task,int workstationId) throws UserAccessException, DoesNotExistException{
		Workstation w = getWorkstation(workstationId);
		for(AssemblyTask t : w.getAllTasks(user)){
			if(t.getType().compareToIgnoreCase(task) == 0){
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

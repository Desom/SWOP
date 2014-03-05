package Main;

import java.util.ArrayList;

public class CarAssemblyProcess {
	
	private final ArrayList<AssemblyTask> tasks;
	
	public CarAssemblyProcess(ArrayList<Option> options){
		ArrayList<AssemblyTask> tasks = new ArrayList<AssemblyTask>();
		for(Option o : options){
			//voeg tasks toe (elke option is gerelateerd met 1 asemblyTask
		}
		
		this.tasks = tasks;
	}
	
	
	protected ArrayList<AssemblyTask> compatibleWith(Workstation station){
		ArrayList<String> acceptedTypes = station.getTaskTypes();
		ArrayList<AssemblyTask> compatibleTypes = new ArrayList<AssemblyTask>();
		for(AssemblyTask t : this.tasks){
			if(acceptedTypes.contains(t.getType())){
				compatibleTypes.add(t);
			}
		}
		return compatibleTypes;
	}

}

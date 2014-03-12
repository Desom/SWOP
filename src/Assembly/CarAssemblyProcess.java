package Assembly;

import java.util.ArrayList;

import Car.Car;
import Car.Option;

public class CarAssemblyProcess {
	
	private final ArrayList<AssemblyTask> tasks;
	private final Car car;
	
	public Car getCar() {
		return car;
	}

	
	public CarAssemblyProcess(Car car, ArrayList<Option> options){
		ArrayList<AssemblyTask> tasks = new ArrayList<AssemblyTask>();
		for(Option o : options){
			ArrayList<String> actions = new ArrayList<String>();
			actions.add("dummy action");
			tasks.add(new AssemblyTask(actions, o.getType()));
		}
		this.tasks = tasks;
		this.car = car;
	}
	
	
	public ArrayList<AssemblyTask> compatibleWith(Workstation station){
		ArrayList<String> acceptedTypes = station.getTaskTypes();
		ArrayList<AssemblyTask> compatibleTypes = new ArrayList<AssemblyTask>();
		for(AssemblyTask t : this.tasks){
			if(acceptedTypes.contains(t.getType())){
				compatibleTypes.add(t);
			}
		}
		return compatibleTypes;
	}


	public Boolean IsCompleted() {
		boolean status = true;
		for(AssemblyTask i: tasks){
			status = status && i.isCompleted();
		}
		return status;
	}

}

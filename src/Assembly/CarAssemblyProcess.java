package Assembly;

import java.util.ArrayList;

import Car.Car;
import Car.Option;

public class CarAssemblyProcess {
	
	private final ArrayList<AssemblyTask> tasks;
	private final Car car;
	
	/**
	 * 
	 * @return The car related to this assembly process
	 */
	public Car getCar() {
		return car;
	}

	public CarAssemblyProcess(Car car, ArrayList<Option> options){
		this(car, options, false);
	}
	
	/**
	 * 
	 * @param car The car related to this assembly process
	 * @param options The options that are to be converted into assemblyTasks
	 * @param isCompleted Indicate if the carassemblyprocess is already completed
	 */
	public CarAssemblyProcess(Car car, ArrayList<Option> options, boolean isCompleted){
		ArrayList<AssemblyTask> tasks = new ArrayList<AssemblyTask>();
		for(Option o : options){
			ArrayList<String> actions = new ArrayList<String>();
			actions.add("dummy action");
			tasks.add(new AssemblyTask(actions, o.getType().toString(), isCompleted)); // to string laten vallen of niet?
		}
		this.tasks = tasks;
		this.car = car;
	}
	
	/**
	 * Get the tasks of this assemblyprocess that are compatible with the given workstation.
	 * 
	 * @param station The workstation to check against.
	 * @return An arraylist of assemblytasks compatible with the given workstation.
	 */
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


	/**
	 * Test if this assemblyprocess is completed.
	 * @return
	 */
	public Boolean IsCompleted() {
		boolean status = true;
		for(AssemblyTask i: tasks){
			status = status && i.isCompleted();
		}
		return status;
	}

}

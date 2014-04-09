package domain.assembly;

import java.util.ArrayList;

import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.Car;

public class CarAssemblyProcess {
	
	private final ArrayList<AssemblyTask> tasks;
	private final Car car;
	private int minutesWorked = 0;

	/**
	 * The constructor for the CarAssemblyProcess class
	 * 
	 * @param car 
	 * 				The Car related to this assembly process
	 * @param options 
	 * 				The Options that are to be converted into assemblyTasks
	 */
	public CarAssemblyProcess(Car car, ArrayList<Option> options){
		this(car, options, false);
	}
	
	/**
	 * The constructor for the CarAssemblyProcess class
	 * 
	 * @param car 
	 * 				The Car related to this assembly process
	 * @param options 
	 * 				The Options that are to be converted into assemblyTasks
	 * @param isCompleted 
	 * 				Indicate if the CarAssemblyProcess is already completed
	 */
	public CarAssemblyProcess(Car car, ArrayList<Option> options, boolean isCompleted){
		ArrayList<AssemblyTask> tasks = new ArrayList<AssemblyTask>();
		for(Option o : options){
			ArrayList<String> actions = new ArrayList<String>();
			actions.add("dummy action");
			tasks.add(new AssemblyTask(actions, o.getType(), isCompleted, this)); 
		}
		this.tasks = tasks;
		this.car = car;
	}
	
	/**
	 * Get the tasks of this AssemblyProcess that are compatible with the given workstation.
	 * 
	 * @param station
	 * 			The workstation to check against.
	 * @return An ArrayList of AssemblyTasks compatible with the given Workstation.
	 */
	protected ArrayList<AssemblyTask> compatibleWith(Workstation station){
		ArrayList<OptionType> acceptedTypes = station.getTaskTypes();
		ArrayList<AssemblyTask> compatibleTypes = new ArrayList<AssemblyTask>();
		for(AssemblyTask t : this.tasks){
			if(acceptedTypes.contains(t.getType())){
				compatibleTypes.add(t);
			}
		}
		return compatibleTypes;
	}


	/**
	 * Test if this AssemblyProcess is completed.
	 * 
	 * @return true if all tasks are completed, otherwise false.
	 */
	public Boolean isCompleted() {
		boolean status = true;
		for(AssemblyTask i: tasks){
			status = status && i.isCompleted();
		}
		return status;
	}

	/**
	 * Add an amount of time spent working on this assemblyProcess (in minutes).
	 * 
	 * @param minutes
	 * 			The amount of time in minutes that has to be added to the total amount of time spent on this configuration.
	 */
	public void addTimeWorked(int minutes){
		this.minutesWorked += minutes;
	}
	
	/**
	 * Get the total amount of time (in minutes) spend working on this assembly process.
	 * 
	 * @return The amount of time (in minutes) spend working on this assembly process
	 */
	public int getTotalTimeSpend(){
		return minutesWorked;
	}
	
	/**
	 * 
	 * @return The Car related to this assembly process
	 */
	public Car getCar() {
		return car;
	}
}

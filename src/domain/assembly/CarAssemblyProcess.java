package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.Order;

public class CarAssemblyProcess {
	
	private final ArrayList<AssemblyTask> tasks;
	private final Order order;
	private int minutesWorked = 0;
	private int delay = -1;
	private GregorianCalendar deliveredTime;

	/**
	 * The constructor of CarAssemblyProcess.
	 * Is by default not completed.
	 * 
	 * @param order 
	 * 			The order related to this assembly process.
	 * @param options 
	 * 			The options that are to be converted into assembly tasks.
	 */
	public CarAssemblyProcess(Order order, ArrayList<Option> options){
		this(order, options, false);
	}
	
	/**
	 * The constructor for the CarAssemblyProcess class
	 * 
	 * @param order 
	 * 			The order related to this assembly process.
	 * @param options 
	 * 			The options that are to be converted into assembly tasks.
	 * @param isCompleted 
	 * 			Indicates if the car assembly process is already completed.
	 */
	public CarAssemblyProcess(Order order, ArrayList<Option> options, boolean isCompleted){
		ArrayList<AssemblyTask> tasks = new ArrayList<AssemblyTask>();
		for(Option o : options){
			ArrayList<String> actions = new ArrayList<String>();
			actions.add("dummy action");
			tasks.add(new AssemblyTask(actions, o.getType(), isCompleted, this)); 
		}
		this.tasks = tasks;
		this.order = order;
	}
	/**
	 * The constructor for the CarAssemblyProcess class
	 * 
	 * @param order 
	 * 			The order related to this assembly process.
	 * @param options 
	 * 			The options that are to be converted into assembly tasks.
	 * @param isCompleted 
	 * 			Indicates if the car assembly process is already completed.
	 * @param deliveredTime
	 * 			The time this CarAssemblyproces was completed
	 */
	public CarAssemblyProcess(Order order, ArrayList<Option> options,
			boolean isCompleted, GregorianCalendar deliveredTime) {
		this(order,options,isCompleted);
		if(isCompleted){
			this.deliveredTime =deliveredTime;
		}
	}

	/**
	 * Get the tasks of this AssemblyProcess that are compatible with the given workstation.
	 * 
	 * @param workstation
	 * 		The workstation to check against.
	 * @return all assembly tasks on which can be worked on in the given workstation.
	 */
	protected ArrayList<AssemblyTask> compatibleWith(Workstation workstation){
		ArrayList<OptionType> acceptedTypes = workstation.getTaskTypes();
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
	 * @return The order related to this assembly process
	 */
	public Order getOrder() {
		return this.order;
	}
	
	/**
	 * This method filters all workstations not usefull for this assembly process from
	 * 		 the given list and returns the remainder.
	 * 
	 * @param stations list of all workstations
	 * @return The list of all workstations required to complete all the tasks of this car assembly process.
	 */
	public ArrayList<Workstation> filterWorkstations(List<Workstation> stations){
		ArrayList<Workstation> filtered = new ArrayList<Workstation>();
		for(Workstation w : stations){
			if(compatibleWith(w).size() > 0){
				filtered.add(w);
			}
		}
		return filtered;
	}
	
	/**
	 * calculate and set the total delay this car order has accumulated at this point (in minutes).
	 * 
	 */
	void registerDelay(List<Workstation> workstations){
		this.delay = this.getTotalTimeSpend() - this.order.getConfiguration().getExpectedWorkingTime()*this.filterWorkstations(workstations).size();
	}

	public int getDelay() {
		return this.delay;
	}
	
	/**
	 * Sets the time the car of this order was delivered.
	 * 
	 * @param	user
	 * 			The user that has ordered the delivery.
	 * @param 	deliveredTime
	 * 			The time the car of this order was delivered.
	 */
	void setDeliveredTime(GregorianCalendar deliveredTime) {
			if(!this.isCompleted())
				throw new IllegalStateException("Can't set deliveredTime because this CarOrder is not completed yet.");
			if(this.deliveredTime!=null)
				throw new IllegalStateException("DeliveredTime already set");
			this.deliveredTime = (GregorianCalendar) deliveredTime.clone();
	}
	
	/**
	 * Returns the time the car of this order was delivered.
	 * 
	 * @return	the time the car of this order was delivered
	 * @throws	IllegalStateException
	 * 			If this car of this order hasn't been delivered yet.
	 */
	public GregorianCalendar getDeliveredTime() throws IllegalStateException{
		if (deliveredTime == null)
			throw new IllegalStateException("This car hasn't been delivered yet");
		return (GregorianCalendar) deliveredTime.clone();
	}
}

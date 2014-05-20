package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.configuration.Option;
import domain.configuration.Taskable;
import domain.order.Order;

public class VehicleAssemblyProcess {
	
	private final ArrayList<AssemblyTask> tasks;
	private final Order order;
	private int minutesWorked = 0;
	private int delay = -1;
	private GregorianCalendar deliveredTime;

	/**
	 * The constructor of VehicleAssemblyProcess.
	 * Is by default not completed.
	 * 
	 * @param order 
	 * 			The order related to this assembly process.
	 * @param tasks
	 * 			The taskables that are to be converted into assembly tasks.
	 */
	public VehicleAssemblyProcess(Order order, ArrayList<Taskable> tasks){
		this(order, tasks, false);
	}
	
	/**
	 * The constructor for the VehicleAssemblyProcess class
	 * 
	 * @param order 
	 * 			The order related to this assembly process.
	 * @param tasks
	 * 			The taskables that are to be converted into assembly tasks.
	 * @param isCompleted 
	 * 			Indicates if the vehicle assembly process is already completed.
	 */
	public VehicleAssemblyProcess(Order order, ArrayList<Taskable> tasks, boolean isCompleted){
		ArrayList<AssemblyTask> assemblyTasks = new ArrayList<AssemblyTask>();
		for(Taskable t : tasks){
			ArrayList<String> actions = new ArrayList<String>();
			actions.add("dummy action");
			assemblyTasks.add(new AssemblyTask(actions, t, isCompleted, this)); 
		}
		this.tasks = assemblyTasks;
		this.order = order;
	}
	/**
	 * The constructor for the VehicleAssemblyProcess class
	 * 
	 * @param order 
	 * 			The order related to this assembly process.
	 * @param tasks 
	 * 			The taskables that are to be converted into assembly tasks.
	 * @param isCompleted 
	 * 			Indicates if the vehicle assembly process is already completed.
	 * @param deliveredTime
	 * 			The time this VehicleAssemblyProcess was completed
	 */
	public VehicleAssemblyProcess(Order order, ArrayList<Taskable> tasks,
			boolean isCompleted, GregorianCalendar deliveredTime) {
		this(order,tasks,isCompleted);
		if(isCompleted){
			this.deliveredTime =deliveredTime;
		}
	}

	/**
	 * Test if this AssemblyProcess is completed.
	 * 
	 * @return true if all tasks are completed and the delivered time is set, otherwise false.
	 */
	public Boolean isCompleted() {
		boolean status = allPartsDone();
		return status && this.deliveredTime != null;
	}
	/**
	 * Test if this AssemblyProcess's tasks are completed.
	 * @return true if all tasks are completed, otherwise false.
	 */
	private boolean allPartsDone() {
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
	 * Returns the order related to this assembly process.
	 * 
	 * @return The order related to this assembly process.
	 */
	public Order getOrder() {
		return this.order;
	}
	
	/**
	 * Calculates and sets the total delay this assembly process has accumulated at this point (in minutes).
	 * 
	 * @param assemblyLine
	 * 		The assembly line which handled this assembly process.
	 */
	void registerDelay(AssemblyLine assemblyLine){
		this.delay = this.getTotalTimeSpend() - this.order.getConfiguration().getExpectedWorkingTimes(assemblyLine.filterWorkstations(this));
	}

	public int getDelay() {
		return this.delay;
	}
	
	/**
	 * Returns the assembly tasks of this vehicle assembly process.
	 * 
	 * @return The assembly tasks of this vehicle assembly process.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<AssemblyTask> getAssemblyTasks() {
		return (ArrayList<AssemblyTask>) this.tasks.clone();
	}
	
	/**
	 * Sets the time the vehicle of this order was delivered.
	 * 
	 * @param 	deliveredTime
	 * 			The time the vehicle of this order was delivered.
	 */
	void setDeliveredTime(GregorianCalendar deliveredTime) {
			if(!this.allPartsDone())
				throw new IllegalStateException("Can't set deliveredTime because this Order is not completed yet.");
			if(this.deliveredTime!=null)
				throw new IllegalStateException("DeliveredTime already set");
			this.deliveredTime = (GregorianCalendar) deliveredTime.clone();
	}
	
	/**
	 * Returns the time this order was delivered.
	 * 
	 * @return	the time this order was delivered
	 * @throws	IllegalStateException
	 * 			If this order hasn't been delivered yet.
	 */
	public GregorianCalendar getDeliveredTime() throws IllegalStateException{
		if (deliveredTime == null)
			throw new IllegalStateException("This order hasn't been delivered yet");
		return (GregorianCalendar) deliveredTime.clone();
	}
}

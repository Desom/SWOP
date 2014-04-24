package domain.assembly;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.configuration.Option;
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
	 * Test if this AssemblyProcess is completed.
	 * 
	 * @return true if all tasks are completed, otherwise false.
	 */
	public Boolean isCompleted() {
		boolean status = true;
		for(AssemblyTask i: tasks){
			status = status && i.isCompleted();
		}
		return status && deliveredTime != null;
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
	 * Calculates and sets the total delay this car order has accumulated at this point (in minutes).
	 * 
	 * @param assemblyLine
	 * 		The assembly line which handled this assembly process.
	 */
	void registerDelay(AssemblyLine assemblyLine){
		this.delay = this.getTotalTimeSpend() - this.order.getConfiguration().getExpectedWorkingTime()*assemblyLine.filterWorkstations(this).size();
	}

	public int getDelay() {
		return this.delay;
	}
	
	/**
	 * Returns the assembly tasks of this car assembly process.
	 * 
	 * @return The assembly tasks of this car assembly process.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<AssemblyTask> getAssemblyTasks() {
		return (ArrayList<AssemblyTask>) this.tasks.clone();
	}
	
	/**
	 * Sets the time the car of this order was delivered.
	 * 
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

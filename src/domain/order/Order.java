package domain.order;

import java.util.GregorianCalendar;
import java.util.List;

import domain.assembly.CarAssemblyProcess;
import domain.assembly.Workstation;
import domain.configuration.Configuration;
import domain.user.User;

public abstract class Order {

	private int orderId;
	private User user;
	private Configuration configuration;
	private CarAssemblyProcess assemblyProcess;
	private GregorianCalendar orderedTime;
	private GregorianCalendar deliveredTime;
	private int delay = -1;
	
	
	/**
	 * Constructor of Order.
	 * Constructs an order which is not completed.
	 * 
	 * @param	orderId
	 * 			The id of this order.
	 * @param	user
	 * 			The user who ordered this order.
	 * @param	configuration
	 * 			The configuration of this order.
	 */
	public Order(int orderId, User user, Configuration configuration) {
		this(orderId, user, configuration, false);
	}
	
	/**
	 * Constructor of Order.
	 * 
	 * @param	orderId
	 * 			The id of this order.
	 * @param	user
	 * 			The user who ordered this order.
	 * @param	configuration
	 * 			The configuration of this order.
	 * @param	isDelivered
	 * 			True if the order has been delivered already, otherwise false.
	 */
	public Order(int orderId, User user, Configuration configuration, boolean isDelivered) {
		this.orderId = orderId;
		this.user = user;
		this.configuration = configuration;
		this.assemblyProcess = new CarAssemblyProcess(this, configuration.getAllOptions(), isDelivered);
	}
	
	/**
	 * Sets the time the order was placed.
	 * 
	 * @param orderedTime
	 * 		The time the order was placed.
	 */
	protected void setOrderedTime(GregorianCalendar orderedTime) {
		this.orderedTime = (GregorianCalendar) orderedTime.clone();
	}
	
	/**
	 * Returns the time the order was placed.
	 * 
	 * @return	the time the order was placed
	 */
	public GregorianCalendar getOrderedTime() {
		return (GregorianCalendar) this.orderedTime.clone();
	}
	
	/**
	 * Sets the time the car of this order was delivered.
	 * 
	 * @param	user
	 * 			The user that has ordered the delivery.
	 * @param 	deliveredTime
	 * 			The time the car of this order was delivered.
	 */
	public void setDeliveredTime(GregorianCalendar deliveredTime) {
			if(!this.isCompleted())
				throw new IllegalStateException("Can't set deliveredTime because this CarOrder is not completed yet.");
			if(this.deliveredTime!=null)
				throw new IllegalStateException("DeliveredTime already set");
			this.deliveredTime = deliveredTime;
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
	
	/**
	 * Returns if this car is already completed or not.
	 * 
	 * @return true if the car is already completed, else false
	 */
	public Boolean isCompleted() {
		return this.assemblyProcess.isCompleted();
	}
	
	/**
	 * Returns the id of this car order.
	 * 
	 * @return the id of this car order
	 */
	public int getCarOrderID() {
		return this.orderId;
	}
	
	/**
	 * Returns the assembly process of this order.
	 * 
	 * @return	the assembly process of this order
	 */
	public CarAssemblyProcess getAssemblyprocess(){
		return this.assemblyProcess;
	}
	
	/**
	 * Returns the Configuration of this order.
	 * 
	 * @return	the Configuration of this order
	 */
	public Configuration getConfiguration() {
		return configuration;
	}
	
	/**
	 * Returns the user id of the user that has placed the order.
	 * 
	 * @return	the user id of the user that has placed the order
	 */
	public int getUserId() {
		return this.user.getId();
	}
	
	/**
	 * calculate and set the total delay this car order has accumulated at this point (in minutes).
	 * 
	 */
	public void registerDelay(List<Workstation> workstations){
		this.delay = this.assemblyProcess.getTotalTimeSpend() - this.configuration.getExpectedWorkingTime()*this.assemblyProcess.filterWorkstations(workstations).size();
	}
	
	/**
	 * Get the previously set delay accumulated for this order.
	 * @return the delay of this order.
	 */
	public int getDelay(){
		return this.delay;
	}
	
	/**
	 * Returns a string representation of the CarOrder
	 */
	@Override
	public String toString(){
		String ordered = "  Ordered on: " + this.orderedTime.get(GregorianCalendar.DAY_OF_MONTH) 
				+ "-" + this.orderedTime.get(GregorianCalendar.MONTH)
				+ "-" + this.orderedTime.get(GregorianCalendar.YEAR)
				+ " " + this.orderedTime.get(GregorianCalendar.HOUR_OF_DAY)
				+ ":" + this.orderedTime.get(GregorianCalendar.MINUTE)
				+ ":" + this.orderedTime.get(GregorianCalendar.SECOND);
		String delivered ="";
		if(this.deliveredTime != null){
			delivered = "  Delivered on: " + this.deliveredTime.get(GregorianCalendar.DAY_OF_MONTH) 
					+ "-" + this.deliveredTime.get(GregorianCalendar.MONTH)
					+ "-" + this.deliveredTime.get(GregorianCalendar.YEAR)
					+ " " + this.deliveredTime.get(GregorianCalendar.HOUR_OF_DAY)
					+ ":" + this.deliveredTime.get(GregorianCalendar.MINUTE)
					+ ":" + this.deliveredTime.get(GregorianCalendar.SECOND);
		}
		return "CarOrder: " + this.orderId + "  User: " + this.getUserId() + ordered + delivered;
		
	}
}

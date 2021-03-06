package domain.scheduling.order;

import java.util.GregorianCalendar;

import domain.assembly.workstations.VehicleAssemblyProcess;
import domain.configuration.Configuration;
import domain.user.User;

public abstract class Order {

	private int orderId;
	private User user;
	private Configuration configuration;
	private VehicleAssemblyProcess assemblyProcess;
	private GregorianCalendar orderedTime;
	
	/**
	 * Constructor of Order.
	 * Constructs an order which is not completed.
	 * 
	 * @param orderId
	 * 		The id of this order.
	 * @param user
	 * 		The user who ordered this order.
	 * @param configuration
	 * 		The configuration of this order.
	 * @param orderedTime
	 * 		The time when it was ordered.
	 */
	public Order(int orderId, User user, Configuration configuration, GregorianCalendar orderedTime) {
		this(orderId, user, configuration, orderedTime, null, false);
	}
	
	/**
	 * Constructor of Order.
	 * 
	 * @param orderId
	 * 		The id of this order.
	 * @param user
	 * 		The user who ordered this order.
	 * @param configuration
	 * 		The configuration of this order.
	 * @param orderedTime
	 * 		The time when it was ordered
	 * @param deliveredTime
	 * 		The time when it was delivered
	 * @param isDelivered
	 * 		True if the order has been delivered already, otherwise false.
	 * @throws IllegalArgumentException
	 * 		If the given configuration is not completed yet.
	 * 		If the deliveredTime is before the orderedTime
	 */
	public Order(int orderId, User user, Configuration configuration, GregorianCalendar orderedTime, GregorianCalendar deliveredTime, boolean isDelivered) 
			throws IllegalArgumentException {
		this.orderId = orderId;
		this.user = user;
		if (!configuration.isCompleted())
			throw new IllegalArgumentException("The given configuration is not completed yet.");
		this.configuration = configuration;
		if (deliveredTime != null && deliveredTime.before(orderedTime))
			throw new IllegalArgumentException("The delivered time is older then the ordered time.");
		this.assemblyProcess = new VehicleAssemblyProcess(this, configuration.getAllTaskables(), isDelivered, deliveredTime);
		this.setOrderedTime(orderedTime);
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
	 * @return The time the order was placed.
	 */
	public GregorianCalendar getOrderedTime() {
		return (GregorianCalendar) this.orderedTime.clone();
	}
	
	/**
	 * Returns the time this order was delivered.
	 * 
	 * @return The time this order was delivered.
	 * @throws IllegalStateException
	 * 		If this order hasn't been delivered yet.
	 */
	public GregorianCalendar getDeliveredTime() throws IllegalStateException{
		return this.getAssemblyprocess().getDeliveredTime();
	}
	
	/**
	 * Returns if this order is already completed or not.
	 * 
	 * @return True if the order is already completed, otherwise false.
	 */
	public Boolean isCompleted() {
		return this.assemblyProcess.isCompleted();
	}
	
	/**
	 * Returns the id of this order.
	 * 
	 * @return The id of this order.
	 */
	public int getOrderID() {
		return this.orderId;
	}
	
	/**
	 * Returns the user who ordered this order.
	 * 
	 * @return The user who ordered this order.
	 */
	public User getUser() {
		return this.user;
	}
	
	/**
	 * Returns the assembly process of this order.
	 * 
	 * @return The assembly process of this order.
	 */
	public VehicleAssemblyProcess getAssemblyprocess(){
		return this.assemblyProcess;
	}
	
	/**
	 * Returns the configuration of this order.
	 * 
	 * @return The configuration of this order.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}
	
	/**
	 * Returns the user id of the user that has placed the order.
	 * 
	 * @return The user id of the user that has placed the order.
	 */
	public int getUserId() {
		return this.user.getId();
	}
	
	/**
	 * Returns the previously set delay accumulated for this order.
	 * 
	 * @return The delay of this order.
	 */
	public int getDelay(){
		return this.getAssemblyprocess().getDelay();
	}
	
	/**
	 * Returns a string representation of the Order
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
		try{
		if(this.getDeliveredTime() != null){
			delivered = "  Delivered on: " + this.getDeliveredTime().get(GregorianCalendar.DAY_OF_MONTH) 
					+ "-" + this.getDeliveredTime().get(GregorianCalendar.MONTH)
					+ "-" + this.getDeliveredTime().get(GregorianCalendar.YEAR)
					+ " " + this.getDeliveredTime().get(GregorianCalendar.HOUR_OF_DAY)
					+ ":" + this.getDeliveredTime().get(GregorianCalendar.MINUTE)
					+ ":" + this.getDeliveredTime().get(GregorianCalendar.SECOND);
		}
		}catch(IllegalStateException e){
			
		};
		return "Order: " + this.orderId + " is ordered by User: " + this.getUserId() + ordered + delivered;
	}
}

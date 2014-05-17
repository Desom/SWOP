package domain.order;

import java.util.GregorianCalendar;

import domain.configuration.Configuration;
import domain.configuration.OptionType;
import domain.user.CustomShopManager;

public class SingleTaskOrder extends Order {
	
	private GregorianCalendar deadline;

	/**
	 * Constructor of SingleTaskOrder.
	 * 
	 * @param singleTaskOrderId
	 * 		The id of the car order.
	 * @param customShopManager
	 * 		The custom shop manager who ordered this order.
	 * @param configuration
	 * 		The configuration of this order.
	 * @param orderedTime
	 * 		The time on which this order was ordered.
	 * @param deadline
	 * 		The deadline for completion of this order.
	 * @throws IllegalArgumentException
	 * 		If the given configuration is not completed yet.
	 * 		If the deadline is before the orderedTime
	 * 		If the deadline is null.
	 */
	public SingleTaskOrder(int singleTaskOrderId, CustomShopManager customShopManager, Configuration configuration, GregorianCalendar orderedTime, GregorianCalendar deadline) throws IllegalArgumentException{
		super(singleTaskOrderId, customShopManager, configuration, orderedTime);
		if (deadline == null){
			throw new IllegalArgumentException("The deadline cannot be null.");
		}
		if (deadline != null && deadline.before(orderedTime))
			throw new IllegalArgumentException("The deadline time is older then the ordered time.");
		this.deadline = (GregorianCalendar) deadline.clone();
	}
	
	
	/**
	 * Constructor of SingleTaskOrder.
	 * 
	 * @param singleTaskOrderId
	 * 		The id of the car order.
	 * @param customShopManager
	 * 		The custom shop manager who ordered this order.
	 * @param configuration
	 * 		The configuration of this order.
	 * @param deadline
	 * 		The deadline for completion of this order.
	 * @param orderedTime
	 * 		The time on which this order was ordered.
	 * @param deliveredTime
	 * 		The time when it was delivered
	 * @param isDelivered
	 * 		True if the order has been delivered already, otherwise false.
	 * @throws IllegalArgumentException
	 * 		If the given configuration is not completed yet.
	 * 		If the deadline is before the orderedTime
	 * 		If the deadline is null.
	 * 		If the deliveredTime is before the orderedTime
	 */
	public SingleTaskOrder(int singleTaskOrderId, CustomShopManager customShopManager, Configuration configuration, GregorianCalendar deadline, GregorianCalendar orderedTime, GregorianCalendar deliveredTime, boolean isDelivered) throws IllegalArgumentException {
		super(singleTaskOrderId, customShopManager, configuration, orderedTime, deliveredTime, isDelivered);
		this.deadline = (GregorianCalendar) deadline.clone();
	}

	/**
	 * Returns the deadline of this order.
	 * 
	 * @return the deadline of this order
	 */
	public GregorianCalendar getDeadLine() {
		return (GregorianCalendar) this.deadline.clone();
	}

	//TODO taskType van maken later?
	/**
	 * Returns the OptionType of the task that is ordered.
	 * 
	 * @return The OptionType of the task.
	 */
	public OptionType getType() {
		return this.getConfiguration().getAllOptions().get(0).getType();
	}
}

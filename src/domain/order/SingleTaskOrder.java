package domain.order;

import java.util.GregorianCalendar;

import domain.configuration.Configuration;
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
	 */
	public SingleTaskOrder(int singleTaskOrderId, CustomShopManager customShopManager, Configuration configuration, GregorianCalendar orderedTime, GregorianCalendar deadline) {
		super(singleTaskOrderId, customShopManager, configuration, orderedTime);
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
}

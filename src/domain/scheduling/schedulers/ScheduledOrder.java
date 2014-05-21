package domain.scheduling.schedulers;

import domain.scheduling.*;
import domain.scheduling.order.Order;

import java.util.GregorianCalendar;

public class ScheduledOrder {

	private GregorianCalendar scheduledTime;
	private Order scheduledOrder;

	/**
	 * Constructor of ScheduledTask.
	 * 
	 * @param scheduledTime
	 * 		The time on which the order will be placed on an assembly line.
	 * @param scheduledOrder
	 * 		The order associated with the scheduled time.
	 */
	public ScheduledOrder(GregorianCalendar scheduledTime, Order scheduledOrder){
		this.scheduledTime = (GregorianCalendar) scheduledTime.clone();
		this.scheduledOrder = scheduledOrder;
	}

	/**
	 * Returns the time on which the order will be placed on an assembly line.
	 * 
	 * @return the time on which the order will be placed on an assembly line
	 */
	public GregorianCalendar getScheduledTime() {
		return (GregorianCalendar) scheduledTime.clone();
	}

	/**
	 * Returns the order associated with the scheduled time.
	 * 
	 * @return the order associated with the scheduled time
	 */
	public Order getScheduledOrder() {
		return scheduledOrder;
	}
}

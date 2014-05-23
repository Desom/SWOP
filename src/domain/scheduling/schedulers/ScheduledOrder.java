package domain.scheduling.schedulers;

import domain.scheduling.order.Order;

import java.util.GregorianCalendar;

public class ScheduledOrder {

	private GregorianCalendar scheduledTime;
	private Order scheduledOrder;
	private GregorianCalendar completedTime = null;

	/**
	 * Constructor of ScheduledTask.
	 * 
	 * @param scheduledTime
	 * 		The time on which the order will be placed on an assembly line.
	 * @param scheduledOrder
	 * 		The order associated with the scheduled time.
	 */
	public ScheduledOrder(GregorianCalendar scheduledTime, Order scheduledOrder){
		if(scheduledTime ==null) this.scheduledTime = null;
		else{
			this.scheduledTime = (GregorianCalendar) scheduledTime.clone();
		}
		this.scheduledOrder = scheduledOrder;
	}

	/**
	 * Returns the time on which the order will be placed on an assembly line.
	 * 
	 * @return The time on which the order will be placed on an assembly line.
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
	
	/**
	 * Sets the time on which the order is expected to be completed.
	 */
	public void setCompletedTime(GregorianCalendar completedTime){
		if(completedTime!=null && this.scheduledOrder != null) this.completedTime = (GregorianCalendar) completedTime.clone();
	}
	/**
	 * Returns the time on which the order is expected to be completed.
	 * 
	 * @return The time on which the order is expected to be completed.
	 */
	public GregorianCalendar getCompletedTime() {
		if(completedTime == null) return null;
		return (GregorianCalendar) completedTime.clone();
	}
}

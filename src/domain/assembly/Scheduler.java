package domain.assembly;

import java.util.GregorianCalendar;

import domain.order.Order;
import domain.order.SingleTaskOrder;

public interface Scheduler {
	
	/**
	 * Returns the current time of the system.
	 * 
	 * @return The current time of the system.
	 */
	public GregorianCalendar getCurrentTime();
	
	/**
	 * Returns the estimated time on which the given order will be completed.
	 * 
	 * @param order
	 * 		The order for which the completion estimate has to be returned.
	 * @return The estimated time on which the given order will be completed.
	 */
	public GregorianCalendar completionEstimate(Order order);
	
	/**
	 * Sets the order manager.
	 * 
	 * @param orderHandler
	 * 		The order manager of this scheduler.
	 */
	public void setOrderHandler(OrderHandler orderHandler);
	
	/**
	 * Signals the scheduler that the schedule needs to be updated.
	 */
	public void updateSchedule();
	
	/**
	 * Checks if the given SingleTaskOrder can be completed before its deadline passes without compromising other deadlines.
	 * 
	 * @param orderWithDeadline
	 * 		The SingleTaskOrder whose deadline will checked.
	 * @return True if the given SingleTaskOrder can be completed before the deadline,
	 * 		false if it can't be completed before the deadline,
	 * 		false if completing this SingleTaskOrder would compromise other deadlines.
	 */
	public boolean canFinishOrderBeforeDeadline(SingleTaskOrder orderWithDeadline);
	
	/**
	 * Checks if this Scheduler has the means to complete the given order.
	 * 
	 * @param order
	 * 		The order for which will be checked.
	 * @return True if the order can be completed, false otherwise.
	 */
	public boolean canScheduleOrder(Order order);
}

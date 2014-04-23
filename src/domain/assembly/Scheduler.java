package domain.assembly;

import java.util.GregorianCalendar;

import domain.order.Order;
import domain.order.OrderManager;

public interface Scheduler {
	
	/**
	 * Returns the current time of the system.
	 * 
	 * @return the current time of the system
	 */
	public GregorianCalendar getCurrentTime();
	
	/**
	 * Returns the estimated time on which the given order will be completed.
	 * 
	 * @param order
	 * 		The order for which the completion estimate has to be returned.
	 * @return the estimated time on which the given order will be completed
	 */
	public GregorianCalendar completionEstimate(Order order);
	
	/**
	 * Sets the order manager.
	 * 
	 * @param orderManager
	 * 		The order manager of this scheduler.
	 */
	public void setOrderManager(OrderManager orderManager);
	
	/**
	 * Updates the schedule.
	 */
	public void updateSchedule();
}

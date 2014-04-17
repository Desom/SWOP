package domain.assembly;

import domain.order.*;

import java.util.GregorianCalendar;

public class ScheduledOrder {

	private GregorianCalendar scheduledTime;
	private Order scheduledOrder;

	public ScheduledOrder(GregorianCalendar scheduledTime, Order scheduledOrder){
		this.scheduledTime = (GregorianCalendar) scheduledTime.clone();
		this.scheduledOrder = scheduledOrder;
	}

	public GregorianCalendar getScheduledTime() {
		return (GregorianCalendar) scheduledTime.clone();
	}

	public Order getScheduledOrder() {
		return scheduledOrder;
	}

}

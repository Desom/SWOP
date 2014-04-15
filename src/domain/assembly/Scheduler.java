package domain.assembly;

import java.util.GregorianCalendar;

import domain.order.Order;

public interface Scheduler {
	
	public GregorianCalendar completionEstimate(Order order);
	
	//TODO wat returned dit? of is deze methode toch onnodig.
	//public ??? getSchedule();
}

package domain.assembly;

import java.util.GregorianCalendar;

import domain.order.Order;

public interface Scheduler {
	
	public Order getNextCarOrder(int time);
	
	public Order seeNextCarOrder(int time);
	
	public GregorianCalendar getCurrentTime();
	
	public GregorianCalendar completionEstimate(Order order);
	
	
	
	//TODO wat returned dit? of is deze methode toch onnodig.
	//public ??? getSchedule();
}

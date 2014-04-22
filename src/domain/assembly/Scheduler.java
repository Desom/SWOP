package domain.assembly;

import java.util.GregorianCalendar;

import domain.order.Order;
import domain.order.OrderManager;

public interface Scheduler {
	
	public GregorianCalendar getCurrentTime();
	
	public GregorianCalendar completionEstimate(Order order);
	
	public void setOrderManager(OrderManager orderManager);
	
	public void updateSchedule();
	
	//TODO wat returned dit? of is deze methode toch onnodig.
	//public ??? getSchedule();
}

package domain.order;

import java.util.GregorianCalendar;

import domain.configuration.Configuration;
import domain.user.User;

public class SingleTaskOrder extends CarOrder {
	private GregorianCalendar deadline;
	public SingleTaskOrder(int carOrderId, User user,
			Configuration configuration, GregorianCalendar deadline) {
		super(carOrderId, user, configuration);
		this.deadline = deadline;
	}
	public GregorianCalendar getDeadLine(){
		return this.deadline;
	}
}

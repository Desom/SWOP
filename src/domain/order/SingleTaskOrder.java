package domain.order;

import java.util.GregorianCalendar;

import domain.configuration.Configuration;
import domain.configuration.OptionType;
import domain.user.User;

public class SingleTaskOrder extends Order {
	
	private GregorianCalendar deadline;

	/**
	 * 
	 * @param carOrderId
	 * @param user
	 * @param configuration
	 * @param orderedTime
	 * @param deadline
	 */
	public SingleTaskOrder(int carOrderId, User user, Configuration configuration, GregorianCalendar orderedTime, GregorianCalendar deadline) {
		super(carOrderId, user, configuration, orderedTime);
		this.deadline = (GregorianCalendar) deadline.clone();
	}

	public GregorianCalendar getDeadLine() {
		return (GregorianCalendar) this.deadline.clone();
	}

	
}

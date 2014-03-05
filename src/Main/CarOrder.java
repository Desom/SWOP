package Main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CarOrder {
	
	private final Car car;
	private final int UserID;
	private final GregorianCalendar time;

	public CarOrder(User user, CarModel model, ArrayList<Option> options) {
		this.car = new Car(model, options);
		this.UserID = user.getId();
		this.time = new GregorianCalendar(); // dit geeft de tijd op het moment van constructie.
	}

	public Object getUserId() {
		return this.UserID;
	}

	public Calendar getOrderedTime() {
		return (Calendar) this.time.clone();
	}

}

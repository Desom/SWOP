package Main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CarOrder {
	
	private final int userID;
	private final GregorianCalendar orderedTime;
	private GregorianCalendar deliveredTime;

	private final Car car;
	
	public CarOrder(User user, CarModel model, ArrayList<Option> options) {
		this.car = new Car(model, options);
		this.userID = user.getId();
		this.orderedTime = new GregorianCalendar(); // dit geeft de tijd op het moment van constructie.
	}

	public CarOrder(int garageHolderId,
			GregorianCalendar orderedCalendar,
			GregorianCalendar deliveredCalendar, CarModel model,
			ArrayList<Option> optionsList) {
		this.userID = garageHolderId;
		this.orderedTime = (GregorianCalendar) orderedCalendar.clone();
		boolean isDelivered;
		if(deliveredCalendar != null){
			this.deliveredTime = (GregorianCalendar) deliveredCalendar.clone();
			isDelivered = true;
		}
		else{
			this.deliveredTime = null;
			isDelivered = false;
		}

		this.car = new Car(model, optionsList, isDelivered);//TODO hoe maken we een geleverde auto.
	}

	private void setDeliveredTime(GregorianCalendar deliveredTime) {
		this.deliveredTime = deliveredTime;
	}

	public int getUserID() {
		return userID;
	}

	public GregorianCalendar getDeliveredTime() {
		return (GregorianCalendar) deliveredTime.clone();
	}

	public Car getCar() {
		return car;
	}

	public int getUserId() {
		return this.userID;
	}

	public GregorianCalendar getOrderedTime() {
		return (GregorianCalendar) this.orderedTime.clone();
	}

}

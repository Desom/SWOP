package Car;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import User.User;

public class CarOrder {
	
	private final int carOrderID;
	private final int userID;
	private final GregorianCalendar orderedTime;
	private GregorianCalendar deliveredTime;

	private final Car car;
	
	/**
	 * Constructor of CarOrder.
	 * 
	 * @param	carOrderId
	 * 			The id of this car order
	 * @param	user
	 * 			The user of that has placed this order
	 * 			// TODO waarom niet gewoon id?
	 * @param	model
	 * 			The model of the car to be ordered
	 * @param	options
	 * 			The options of the car to be ordered
	 */
	public CarOrder(int carOrderId, User user, CarModel model, ArrayList<Option> options) {
		this.carOrderID = carOrderId;
		this.car = new Car(this, model, options);
		this.userID = user.getId();
		this.orderedTime = new GregorianCalendar(); // dit geeft de tijd op het moment van constructie.
	}

	/**
	 * TODO
	 * 
	 * @param carOrderId
	 * @param garageHolderId
	 * @param orderedCalendar
	 * @param deliveredCalendar
	 * @param model
	 * @param optionsList
	 */
	public CarOrder(int carOrderId, int garageHolderId,
			GregorianCalendar orderedCalendar,
			GregorianCalendar deliveredCalendar, CarModel model,
			ArrayList<Option> optionsList) {
		this.carOrderID = carOrderId;
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

		this.car = new Car(this, model, optionsList, isDelivered);//TODO hoe maken we een geleverde auto.
	}

	/**
	 * Sets the time this car was delivered.
	 * 
	 * @param 	deliveredTime
	 * 			the time this car was delivered
	 */
	private void setDeliveredTime(GregorianCalendar deliveredTime) {
		this.deliveredTime = deliveredTime;
	}

	/**
	 * Returns the id of this car order.
	 * 
	 * @return the id of this car order
	 */
	public int getCarOrderID() {
		return carOrderID;
	}

	/**
	 * Returns the time the car was delivered.
	 * 
	 * @return	the time the car was delivered
	 * @throws	IllegalStateException
	 * 			If this car hasn't been delivered yet
	 */
	public GregorianCalendar getDeliveredTime() throws IllegalStateException{
		if (deliveredTime == null)
			throw new IllegalStateException("This car hasn't been delivered yet");
		return (GregorianCalendar) deliveredTime.clone();
	}

	/**
	 * Returns the car that has been ordered.
	 * 
	 * @return	the car that has been ordered
	 */
	public Car getCar() {
		return car;
	}

	/**
	 * Returns the user id of the user that has placed the order.
	 * 
	 * @return	the user id of the user that has placed the order
	 */
	public int getUserId() {
		return this.userID;
	}

	/**
	 * Returns the time the order was placed.
	 * 
	 * @return	the time the order was placed
	 */
	public GregorianCalendar getOrderedTime() {
		return (GregorianCalendar) this.orderedTime.clone();
	}

	public Boolean IsCompleted() {
		// TODO Auto-generated method stub
		return this.getCar().IsCompleted();
	}

}

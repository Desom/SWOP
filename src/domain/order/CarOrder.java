package domain.order;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.configuration.CarModel;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.user.GarageHolder;
import domain.user.User;

public class CarOrder extends Order {
	
	/**
	 * Constructor of CarOrder.
	 * 
	 * @param carOrderId
	 * 		The id of this car order
	 * @param garageHolder
	 * 		The GarageHolder that has placed this order
	 * @param configuration
	 * 		The configuration of the specified car
	 * @param orderedTime
	 * 		The time at which this CarOrder is ordered.
	 */
	public CarOrder(int carOrderId, GarageHolder garageHolder, Configuration configuration, GregorianCalendar orderedTime) {
		super(carOrderId, garageHolder, configuration, false);
		this.setOrderedTime(orderedTime);
	}

	/**
	 * Constructor of CarOrder.
	 * Creates a car using specifying it's ordered and deliveredTime
	 * 
	 * @param carOrderId
	 * 			The id of this car order
	 * @param user
	 * 			The garageHolder that has placed this order
	 * @param orderedCalendar
	 * 			The time when it was ordered
	 * @param deliveredCalendar
	 * 			The time when it was delivered; the car is already completed if deliveredCalendar is not null
	 * @param configuration 
	 */
	public CarOrder(int carOrderId, GarageHolder garageHolder, Configuration configuration, GregorianCalendar orderedTime, GregorianCalendar deliveredTime, boolean isCompleted) {
		super(carOrderId, garageHolder, configuration, isCompleted);
		this.setOrderedTime(orderedTime);
		if(isCompleted){
			this.setDeliveredTime(deliveredTime);			
		}
	}

}

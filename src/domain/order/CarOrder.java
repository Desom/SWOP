package domain.order;

import java.util.GregorianCalendar;

import domain.configuration.Configuration;
import domain.user.GarageHolder;

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
		super(carOrderId, garageHolder, configuration, orderedTime);
	}

	/**
	 * Constructor of CarOrder.
	 * Creates a car using specifying it's ordered and deliveredTime
	 * 
	 * @param carOrderId
	 * 			The id of this car order
	 * @param garageHolder
	 * 			The garageHolder that has placed this order
	 * @param configuration 
	 *	 		The configuration of the specified car
	 * @param orderedTime
	 * 			The time when it was ordered
	 * @param deliveredTime
	 * 			The time when it was delivered; the car is already completed if deliveredCalendar is not null
	 * @param isDelivered
	 * 			True if the car has been delivered, otherwise false.
	 */
	public CarOrder(int carOrderId, GarageHolder garageHolder, Configuration configuration, GregorianCalendar orderedTime, GregorianCalendar deliveredTime, boolean isDelivered) {
		super(carOrderId, garageHolder, configuration, orderedTime, deliveredTime, isDelivered);
	}

}

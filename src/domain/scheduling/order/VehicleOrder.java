package domain.scheduling.order;

import java.util.GregorianCalendar;

import domain.configuration.Configuration;
import domain.user.GarageHolder;

public class VehicleOrder extends Order {
	
	/**
	 * Constructor of VehicleOrder.
	 * 
	 * @param vehicleOrderId
	 * 		The id of this vehicle order
	 * @param garageHolder
	 * 		The GarageHolder that has placed this order
	 * @param configuration
	 * 		The configuration of the specified vehicle
	 * @param orderedTime
	 * 		The time at which this VehicleOrder is ordered.
	 */
	public VehicleOrder(int vehicleOrderId, GarageHolder garageHolder, Configuration configuration, GregorianCalendar orderedTime) {
		super(vehicleOrderId, garageHolder, configuration, orderedTime);
	}

	/**
	 * Constructor of VehicleOrder.
	 * Creates a vehicle using specifying it's ordered and deliveredTime
	 * 
	 * @param vehicleOrderId
	 * 			The id of this vehicle order
	 * @param garageHolder
	 * 			The garageHolder that has placed this order
	 * @param configuration 
	 *	 		The configuration of the specified vehicle
	 * @param orderedTime
	 * 			The time when it was ordered
	 * @param deliveredTime
	 * 			The time when it was delivered; the vehicle is already completed if deliveredCalendar is not null
	 * @param isDelivered
	 * 			True if the vehicle has been delivered, otherwise false.
	 */
	public VehicleOrder(int vehicleOrderId, GarageHolder garageHolder, Configuration configuration, GregorianCalendar orderedTime, GregorianCalendar deliveredTime, boolean isDelivered) {
		super(vehicleOrderId, garageHolder, configuration, orderedTime, deliveredTime, isDelivered);
	}

}

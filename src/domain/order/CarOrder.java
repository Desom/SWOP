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
	 * @param	carOrderId
	 * 			The id of this car order
	 * @param	user TODO
	 * 			The user of that has placed this order
	 * @param	configuration
	 * 			The configuration of the specified car
	 */
	public CarOrder(int carOrderId, GarageHolder garageHolder, Configuration configuration) {
		super(carOrderId, garageHolder, configuration, false);
		this.setOrderedTime(new GregorianCalendar()); // dit geeft de tijd op het moment van constructie.
	}

	/**
	 * Constructor of CarOrder.
	 * Creates a car using model and options
	 * 
	 * @param carOrderId
	 * 			The id of this car order
	 * @param userId
	 * 			The id of the garageHolder that has placed this order
	 * @param orderedCalendar
	 * 			The time when it was ordered
	 * @param deliveredCalendar
	 * 			The time when it was delivered; the car is already completed if deliveredCalendar is not null
	 * @param model
	 * 			The model of the car that has been ordered
	 * @param optionsList
	 * 			The options of the car that has been ordered
	 * @param configuration 
	 */


}

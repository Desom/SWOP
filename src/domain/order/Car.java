package domain.order;

import java.util.ArrayList;

import domain.assembly.CarAssemblyProcess;
import domain.configuration.CarModel;
import domain.configuration.Configuration;
import domain.configuration.Option;

public class Car {
	
	private final Configuration configuration;
	private final CarAssemblyProcess process;
	private final CarOrder order;

	/**
	 * Constructor for a non-delivered Car. 
	 * 
	 * @param	order
	 * 			The order of the car
	 * @param	model
	 * 			The model of the car
	 * @param	options
	 * 			The options of the car
	 */
	public Car(CarOrder order, CarModel model, ArrayList<Option> options) {
		this(order, model,options, false);
	}
	
	/**
	 * Constructor for Car.
	 * 
	 * @param	order
	 * 			The order of the car
	 * @param	model
	 * 			The model of the car
	 * @param	options
	 * 			The options of the car
	 * @param	isDelivered
	 * 			True if the car has been delivered already, otherwise false 
	 */
	public Car(CarOrder order, CarModel model, ArrayList<Option> options, boolean isDelivered){
		this.configuration = new Configuration(model, options);
		this.process = new CarAssemblyProcess(this, options, isDelivered);
		this.order = order;
	}
	
	/**
	 * Get the total delay this car has accumulated at this point (in minutes).
	 * 
	 * @return The total time spend working on this car (in minutes) - the expected time spend working on completing this car (in minutes).
	 */
	public int getDelay(){
		return this.process.getTotalTimeSpend() - this.configuration.getExpectedWorkingTime();
	}

	/**
	 * Returns the order of this car.
	 * 
	 * @return	the order of this car
	 */
	public CarOrder getOrder() {
		return order;
	}
	
	/**
	 * Returns the assembly process of this car.
	 * 
	 * @return	the assembly process of this car
	 */
	public CarAssemblyProcess getAssemblyprocess(){
		return this.process;
	}

	/**
	 * Checks if the construction process of this car is completed.
	 * 
	 * @return	true if construction is completed, otherwise false
	 */
	public Boolean isCompleted() {
		return this.getAssemblyprocess().isCompleted();
	}
	
	/**
	 * Returns the Configuration of this car.
	 * 
	 * @return	the Configuration of this car
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

}

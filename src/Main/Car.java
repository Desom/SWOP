package Main;

import java.util.ArrayList;

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
		this.process = new CarAssemblyProcess(this, this.configuration.getAllOptions());
		this.order = order;
	}

	/**
	 * Returns the order of this car.
	 * 
	 * @return	the order of this car
	 */
	protected CarOrder getOrder() {
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
	public Boolean IsCompleted() {
		return this.getAssemblyprocess().IsCompleted();
	}
	

}

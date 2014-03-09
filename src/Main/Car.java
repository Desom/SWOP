package Main;

import java.util.ArrayList;

public class Car {
	
	private final Configuration configuration;
	private final CarAssemblyProcess process;
	private final CarOrder order;
	
	protected CarOrder getOrder() {
		return order;
	}

	public Car(CarOrder order, CarModel model, ArrayList<Option> options, boolean isDelivered){
		this.configuration = new Configuration(model, options);
		this.process = new CarAssemblyProcess(this, this.configuration.getAllOptions());
		this.order = order;
	}

	public Car(CarOrder order, CarModel model, ArrayList<Option> options) {
		this(order, model,options, false);
	}
	
	protected CarAssemblyProcess getAssemblyprocess(){
		return this.getAssemblyprocess();
	}
	

}

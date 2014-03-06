package Main;

import java.util.ArrayList;

public class Car {
	
	private final Configuration configuration;
	private final CarAssemblyProcess process;
	
	public Car(CarModel model, ArrayList<Option> options, boolean isDelivered){
		this.configuration = new Configuration(model, options);
		this.process = new CarAssemblyProcess(this.configuration.getAllOptions());
	}

	public Car(CarModel model, ArrayList<Option> options) {
		this(model,options, false);
	}
	

}

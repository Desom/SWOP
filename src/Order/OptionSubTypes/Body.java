package Order.OptionSubTypes;
import java.util.ArrayList;

import Car.Option;
import Order.CarModelCatalogException;


public class Body extends Option {

	private final static String type = "Body";
	
	public Body(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws CarModelCatalogException {
		super(description, compatibles, incompatibles);
		// TODO Auto-generated constructor stub
	}
	
	public String getType() {
		return type;
	}
}

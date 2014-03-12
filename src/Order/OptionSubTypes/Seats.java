package Order.OptionSubTypes;
import java.util.ArrayList;

import Car.Option;
import Order.CarModelCatalogException;


public class Seats extends Option {

	private final static String type = "Seats";

	public Seats(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
					throws CarModelCatalogException {
		super(description, compatibles, incompatibles);
	}
	
	public String getType() {
		return type;
	}
}

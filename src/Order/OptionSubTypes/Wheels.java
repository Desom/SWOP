package Order.OptionSubTypes;
import java.util.ArrayList;

import Car.Option;
import Order.CarModelCatalogException;


public class Wheels extends Option {

	public final static String type = "Wheels";
	
	public Wheels(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws CarModelCatalogException {
		super(description, compatibles, incompatibles);
	}
	
	public String getType() {
		return type;
	}
}

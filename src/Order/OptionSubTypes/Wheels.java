package Order.OptionSubTypes;
import java.util.ArrayList;

import Car.Option;
import Order.CarModelCatalogException;


public class Wheels extends Option {

	public final static OptionType type = OptionType.Wheels;
	
	public Wheels(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws CarModelCatalogException {
		super(description, compatibles, incompatibles);
	}
	
	public OptionType getType() {
		return type;
	}
}

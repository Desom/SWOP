package Order.OptionSubTypes;
import java.util.ArrayList;

import Car.Option;
import Order.CarModelCatalogException;


public class Seats extends Option {

	private final static OptionType type = OptionType.Seats;

	public Seats(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
					throws CarModelCatalogException {
		super(description, compatibles, incompatibles);
	}
	
	public OptionType getType() {
		return type;
	}
}

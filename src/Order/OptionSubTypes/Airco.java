package Order.OptionSubTypes;
import java.util.ArrayList;

import Car.Option;
import Order.CarModelCatalogException;


public class Airco extends Option {

	private final static OptionType type = OptionType.Airco;
	
	public Airco(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws CarModelCatalogException {
		super(description, compatibles, incompatibles);
	}
	
	public OptionType getType() {
		return type;
	}

}

package Order.OptionSubTypes;
import java.util.ArrayList;

import Car.Option;
import Order.CarModelCatalogException;


public class Gearbox extends Option {

	private final static OptionType type = OptionType.Gearbox;
	
	public Gearbox(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws CarModelCatalogException {
		super(description, compatibles, incompatibles);
		// TODO Auto-generated constructor stub
	}
	public OptionType getType() {
		return type;
	}
}

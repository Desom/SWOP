package Order.OptionSubTypes;

import java.util.ArrayList;

import Car.Option;
import Order.CarModelCatalogException;

public class Spoiler extends Option {

private final static OptionType type = OptionType.Spoiler;
	
	public Spoiler(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws CarModelCatalogException {
		super(description, compatibles, incompatibles);
	}
	
	public OptionType getType() {
		return type;
	}

}

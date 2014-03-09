package OptionSubTypes;
import java.util.ArrayList;

import Main.Option;
import Main.CarModelCatalogException;


public class Seats extends Option {

	private final static String type = "Seats";

	public Seats(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
					throws CarModelCatalogException {
		super(description, compatibles, incompatibles);
	}
	
	
}

package OptionSubTypes;
import java.util.ArrayList;

import Main.Option;
import Main.CarModelCatalogException;


public class Airco extends Option {

	public static String type = "Airco";
	public Airco(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws CarModelCatalogException {
		super(description, compatibles, incompatibles, "Airco");
	}

}

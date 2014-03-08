package OptionSubTypes;
import java.util.ArrayList;

import Main.Option;
import Main.CarModelCatalogException;


public class Gearbox extends Option {

	public static String type = "Color";
	public Gearbox(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws CarModelCatalogException {
		super(description, compatibles, incompatibles, "Gearbox");
		// TODO Auto-generated constructor stub
	}

}

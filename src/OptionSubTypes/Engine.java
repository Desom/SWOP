package OptionSubTypes;
import java.util.ArrayList;

import Main.Option;
import Main.CarModelCatalogException;


public class Engine extends Option {

	public static String type = "Engine";
	public Engine(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws CarModelCatalogException {
		super(description, compatibles, incompatibles, "Engine");
		// TODO Auto-generated constructor stub
	}

}

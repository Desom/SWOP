package OptionSubTypes;
import java.util.ArrayList;

import Main.Option;
import Main.CarModelCatalogException;


public class Body extends Option {

	public static String type = "Body";
	public Body(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws CarModelCatalogException {
		super(description, compatibles, incompatibles, "Body");
		// TODO Auto-generated constructor stub
	}

}

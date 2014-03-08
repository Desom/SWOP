package OptionSubTypes;
import java.util.ArrayList;

import Main.Option;
import Main.CarModelCatalogException;


public class Color extends Option {
	public static String type = "Color";

	public Color(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws CarModelCatalogException {
		super(description, compatibles, incompatibles, "Color");
		// TODO Auto-generated constructor stub
	}
		
}

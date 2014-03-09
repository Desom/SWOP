package OptionSubTypes;
import java.util.ArrayList;

import Main.Option;
import Main.CarModelCatalogException;


public class Gearbox extends Option {

	private final static String type = "Gearbox";
	
	public Gearbox(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws CarModelCatalogException {
		super(description, compatibles, incompatibles);
		// TODO Auto-generated constructor stub
	}
	public String getType() {
		return type;
	}
}

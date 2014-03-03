package OptionSubTypes;
import java.util.ArrayList;

import Main.Option;
import Main.inconsistent_state_Exception;


public class Gearbox extends Option {

	public Gearbox(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws inconsistent_state_Exception {
		super(description, compatibles, incompatibles);
		// TODO Auto-generated constructor stub
	}

}

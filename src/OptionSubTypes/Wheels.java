package OptionSubTypes;
import java.util.ArrayList;

import Main.Option;
import Main.inconsistent_state_Exception;


public class Wheels extends Option {

	public Wheels(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws inconsistent_state_Exception {
		super(description, compatibles, incompatibles);
	}

}

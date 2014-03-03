package OptionSubTypes;
import java.util.ArrayList;

import Main.Option;
import Main.inconsistent_state_Exception;


public class Body extends Option {

	public Body(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws inconsistent_state_Exception {
		super(description, compatibles, incompatibles);
		// TODO Auto-generated constructor stub
	}

}

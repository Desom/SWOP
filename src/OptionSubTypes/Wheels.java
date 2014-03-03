import java.util.ArrayList;


public class Wheels extends Option {

	public Wheels(String description, ArrayList<Option> compatibles,
			ArrayList<Option> incompatibles)
			throws inconsistent_state_Exception {
		super(description, compatibles, incompatibles);
	}

}

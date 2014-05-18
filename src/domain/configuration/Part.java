package domain.configuration;


public class Part extends TaskType{
	

	/**
	 * Constructor of Part.
	 * 
	 * @param name
	 * 		The name of this Part.
	 * @param singleTaskPossible
	 * 		True if this option is available for single task orders, otherwise false.
	 * @param mandatory
	 * 		True if this option is mandatory for vehicle orders, otherwise false.
	 */
	Part(String name, boolean singleTaskPossible, boolean mandatory){
		super.name = name;
		super.singleTaskPossible = singleTaskPossible;
		super.mandatory =mandatory;
	}

	/**
	 * Constructor of Part.
	 * The part won't be available for single task orders and won't be mandatory for vehicle orders.
	 */
	Part(String name){
		super.name = name;
		super.singleTaskPossible = false;
		super.mandatory = true;
	}



}

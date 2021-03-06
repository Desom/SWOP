package domain.configuration;


public class OptionType implements TaskType{

	private String name;
	private boolean singleTaskPossible;
	private boolean mandatory;
	
	
	/**
	 * Constructor of OptionType.
	 * 
	 * @param name
	 * 		The name of this optionType.
	 * @param singleTaskPossible
	 * 		True if this option is available for single task orders, otherwise false.
	 * @param mandatory
	 * 		True if this option is mandatory for vehicle orders, otherwise false.
	 */
	OptionType(String name, boolean singleTaskPossible, boolean mandatory){
		this.name = name;
		this.singleTaskPossible = singleTaskPossible;
		this.mandatory =mandatory;
	}

	/**
	 * Constructor of OptionType.
	 * The option type will be available for single task orders and woill be mandatory for vehicle orders.
	 * 
	 * @param name
	 * 		The name of this option type.
	 */
	OptionType(String name){
		this.name = name;
		this.singleTaskPossible = false;
		this.mandatory = true;
	}
	
	/**
	 * Returns whether the option type is available for single task orders.
	 * 
	 * @return True if the option type is available for single task orders, otherwise false.
	 */
	public boolean isSingleTaskPossible() {
		return singleTaskPossible;
	}
	
	/**
	 * Returns whether the option type is mandatory for vehicle orders.
	 * 
	 * @return True if the option type is mandatory for vehicle orders, otherwise false.
	 */
	public boolean isMandatory() {
		return mandatory;
	}
	
	
	
	/**
	 * Returns the name of this optiontype.
	 * 
	 * @return the name of this optiontype.
	 */
	public String getName(){
		return this.name;
	}
	
	public String toString(){
		return getName();
	}
}
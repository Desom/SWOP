package controller;

import java.util.List;



public interface CommunicationTool {


	
	/**
	 * get all possible options of a specified type
	 * @param type
	 * @return
	 */
	public List<String> getPossibleOptionsOfType(OrderForm order,String type);
	/**
	 * See if you can place an option of a specified type
	 * @param Type
	 * @return true if you can place an option of a specified type
	 */
	public boolean canPlaceType(OrderForm order,String Type);
	/**
	 * get a list of all option types.
	 * @return a list of all option types.
	 */
	public List<String> getOptionTypes();

}

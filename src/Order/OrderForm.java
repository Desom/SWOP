package Order;

import java.util.ArrayList;
import java.util.List;


public interface OrderForm {
	/**
	 * Gets all the filled-in options stored in the form.
	 * @return
	 */
	public ArrayList<String> getOptions();
	/**
	 * Gets the Car model stored in the form.
	 * @return
	 */
	public String getModel();
	/**
	 * Stores an option in the form.
	 * @param option description of the option you want to store
	 * @return true if you placed the option false otherwise
	 * **/
	public void setOption(String option);
	/**
	 * Get all possible options of a specified type.
	 * @param type
	 * @return
	 */
	public List<String> getPossibleOptionsOfType(String type);
	/**
	 * See if you can place an option of a specified type.
	 * @param Type
	 * @return true if you can place an option of a specified type
	 */
	public boolean canPlaceType(String Type);
	/**
	 * get a list of all option types.
	 * @return a list of all option types.
	 */
	public List<String> getOptionTypes();
}

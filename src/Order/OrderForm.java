package Order;

import java.util.ArrayList;
import java.util.List;

import Car.CarModel;
import Car.Option;
import User.User;

public interface OrderForm {
	/**
	 * Get all the filled-in options stored in the form
	 * @return
	 */
	public ArrayList<Option> getOptions();
	/**
	 * Get the Car model stored in the form
	 * @return
	 */
	public CarModel getModel();
	/**
	 * Get the User stored in the form
	 * @return
	 */
	public User getUser();
	/**
	 * Stores an option in the form
	 * @param option description of the option you want to store
	 * @return true if you placed the option false otherwise
	 * **/
	public boolean setOption(String option);
	/**
	 * get all possible options of a specified type
	 * @param type
	 * @return
	 */
	public List<String> getPossibleOptionsOfType(String type);
	/**
	 * See if you can place an option of a specified type
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

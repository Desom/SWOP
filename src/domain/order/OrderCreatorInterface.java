package domain.order;

import java.io.IOException;
import java.util.ArrayList;

import domain.policies.InvalidConfigurationException;

public interface OrderCreatorInterface {

	/**
	 * Creates all placed Orders.
	 * 
	 * @return a list of all placed Orders.
	 * @throws InvalidConfigurationException
	 * 		If the configuration is invalid.
	 * @throws IOException
	 * 		If a model can't be found.
	 */
	public abstract ArrayList<Order> createOrderList() throws IOException;

}
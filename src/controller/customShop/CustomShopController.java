package controller.customShop;

import java.util.ArrayList;

import controller.UIInterface;
import domain.Company;
import domain.user.CustomShopManager;

public class CustomShopController {
	
	private OrderSingleTaskHandler orderSingleTaskHandler;
	
	/**
	 * Constructor of CustomShopController.
	 * Constructs the appropriate handlers to:
	 * 	1) Order single task
	 */
	public CustomShopController() {
		this.orderSingleTaskHandler = new OrderSingleTaskHandler();
	}

	/**
	 * Runs this CustomShopController object.
	 * 
	 * @param ui
	 * 		The UI used to communicate with the user.
	 * @param company
	 * 		The company that is handling the request of the user.
	 * @param customShopManager
	 * 		The custom shop manager that does a request.
	 */
	public void run(UIInterface ui, Company company, CustomShopManager customShopManager) {
		loop: while(true) {
			ArrayList<String> possibilities = new ArrayList<String>();
			possibilities.add("Order single task");
			possibilities.add("Log out");
			String answer = ui.askWithPossibilities("What do you want to do?", possibilities);
			switch(answer) {
			case "Order single task":	this.orderSingleTaskHandler.run(ui, company, customShopManager);
										break;
			case "Log out":				break loop;
			}
		}
	}

}

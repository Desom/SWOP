package controller.garageHolder;

import java.util.ArrayList;

import controller.UIInterface;
import domain.Company;
import domain.user.GarageHolder;

public class GarageHolderController {
	
	OrderNewCarHandler orderNewCarHandler;
	CheckOrderDetailsHandler checkOrderDetailsHandler;
	
	/**
	 * Constructor of GarageHolderController.
	 * Constructs the appropriate handlers to:
	 * 	1) Order a new car
	 * 	2) Check order details
	 */
	public GarageHolderController() {
		this.orderNewCarHandler = new OrderNewCarHandler();
		this.checkOrderDetailsHandler = new CheckOrderDetailsHandler();
	}

	/**
	 * Runs this GarageHolderController object.
	 * 
	 * @param ui
	 * 		The UI used to communicate with the user.
	 * @param company
	 * 		The company that is handling the request of the user.
	 * @param garageHolder
	 * 		The garage holder that does a request.
	 */
	public void run(UIInterface ui, Company company, GarageHolder garageHolder) {
		loop: while(true) {
			ArrayList<String> possibilities = new ArrayList<String>();
			possibilities.add("Order new car");
			possibilities.add("Check order details");
			possibilities.add("Log out");
			String answer = ui.askWithPossibilities("What do you want to do?", possibilities);
			switch(answer) {
			case "Order new car":		this.orderNewCarHandler.run(ui, company, garageHolder);
			break;
			case "Check order details":	this.checkOrderDetailsHandler.run(ui, company, garageHolder);
			break;
			case "Log out":				break loop;
			}
		}
	}
}

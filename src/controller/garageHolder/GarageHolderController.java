package controller.garageHolder;

import java.util.ArrayList;

import controller.UIInterface;
import domain.Company;
import domain.user.GarageHolder;

public class GarageHolderController {
	
	OrderNewVehicleHandler orderNewVehicleHandler;
	CheckOrderDetailsHandler checkOrderDetailsHandler;
	
	/**
	 * Constructor of GarageHolderController.
	 * Constructs the appropriate handlers to:
	 * 	1) Order a new vehicle
	 * 	2) Check order details
	 */
	public GarageHolderController() {
		this.orderNewVehicleHandler = new OrderNewVehicleHandler();
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
			possibilities.add("Order new vehicle");
			possibilities.add("Check order details");
			possibilities.add("Log out");
			String answer = ui.askWithPossibilities("What do you want to do?", possibilities);
			switch(answer) {
			case "Order new vehicle":		this.orderNewVehicleHandler.run(ui, company, garageHolder);
			break;
			case "Check order details":	this.checkOrderDetailsHandler.run(ui, company, garageHolder);
			break;
			case "Log out":				break loop;
			}
		}
	}
}

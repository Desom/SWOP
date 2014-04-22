package controller.carMechanic;

import java.util.ArrayList;

import controller.UIInterface;
import domain.Company;
import domain.user.CarMechanic;

public class CarMechanicController {

	private CheckAssemblyLineStatusHandler checkAssemblyLineStatusHandler;
	private PerformAssemblyTaskHandler performAssemblyTaskHandler;

	/**
	 * Constructor of CarMechanicController.
	 * Constructs the appropriate handlers to:
	 * 	1) Check assembly line status
	 * 	2) Perform assembly task
	 */
	public CarMechanicController() {
		this.checkAssemblyLineStatusHandler = new CheckAssemblyLineStatusHandler();
		this.performAssemblyTaskHandler = new PerformAssemblyTaskHandler();
	}

	/**
	 * Runs this CarMechanicController object.
	 * 
	 * @param ui
	 * 		The UI used to communicate with the user.
	 * @param company
	 * 		The company that is handling the request of the user.
	 * @param carMechanic
	 * 		The car mechanic that does a request.
	 */
	public void run(UIInterface ui, Company company, CarMechanic carMechanic) {
		loop: while(true) {
			ArrayList<String> possibilities = new ArrayList<String>();
			possibilities.add("Perform assembly tasks");
			possibilities.add("Check assembly line status");
			possibilities.add("Log out");
			String answer = ui.askWithPossibilities("What do you want to do?", possibilities);
			switch(answer) {
			case "Perform assembly tasks":		this.performAssemblyTaskHandler.run(ui, company, carMechanic);
			break;
			case "Check assembly line status":	this.checkAssemblyLineStatusHandler.run(ui, company);
			break;
			case "Log out":						break loop;
			}
		}
	}
}

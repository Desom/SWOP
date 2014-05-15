package controller.manager;

import java.util.ArrayList;

import controller.UIInterface;
import domain.Company;
import domain.user.Manager;

public class ManagerController {

	AdaptSchedulingAlgorithmHandler adaptSchedulingAlgorithmHandler;
	ChangeAssemblyLineStatusHandler changeAssemblyLineStatusHandler;
	CheckProductionStatisticsHandler checkProductionStatisticsHandler;
	
	/**
	 * Constructor of ManagerController.
	 * Constructs the appropriate handlers to:
	 * 	1) Adapt scheduling algorithm
	 * 	2) Check production statistics
	 */
	public ManagerController() {
		adaptSchedulingAlgorithmHandler = new AdaptSchedulingAlgorithmHandler();
		changeAssemblyLineStatusHandler = new ChangeAssemblyLineStatusHandler();
		checkProductionStatisticsHandler = new CheckProductionStatisticsHandler();
	}

	/**
	 * Runs this ManagerController object.
	 * 
	 * @param ui
	 * 		The UI used to communicate with the user.
	 * @param company
	 * 		The company that is handling the request of the user.
	 * @param manager
	 * 		The manager that does a request.
	 */
	public void run(UIInterface ui, Company company, Manager manager) {
		loop: while(true) {
			ArrayList<String> possibilities = new ArrayList<String>();
			possibilities.add("Adapt scheduling algorithm");
			possibilities.add("Change assembly line status");
			possibilities.add("Check production statistics");
			possibilities.add("Log out");
			String answer = ui.askWithPossibilities("What do you want to do?", possibilities);
			switch(answer) {
			case "Adapt scheduling algorithm":		this.adaptSchedulingAlgorithmHandler.run(ui, company, manager);
			break;
			case "Change assembly line status":		this.changeAssemblyLineStatusHandler.run(ui, company);
			break;
			case "Check production statistics":		this.checkProductionStatisticsHandler.run(ui, company, manager);
			break;
			case "Log out":							break loop;
			}
		}
	}
}

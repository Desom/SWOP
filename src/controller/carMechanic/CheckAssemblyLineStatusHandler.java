package controller.carMechanic;

import controller.UIInterface;
import domain.Company;

public class CheckAssemblyLineStatusHandler {

	public void run(UIInterface ui, Company company) {
		ui.showAssemblyLineStatus(company.getAssemblyLine().currentStatus());
	}
	
}

package controller.carMechanic;

import controller.ControllerInterface;
import controller.UIInterface;
import domain.Company;
import domain.user.CarMechanic;

public class CheckAssemblyLineStatusHandler implements ControllerInterface {

	UIInterface ui;
	Company company;
	
	public CheckAssemblyLineStatusHandler(UIInterface ui, Company company) {
		this.ui = ui;
		this.company = company;
	}

	@Override
	public void run() {
		ui.showAssemblyLineStatus(company.getAssemblyLine().currentStatus());
	}
	
}

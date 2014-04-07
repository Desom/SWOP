package controller.carMechanic;

import controller.ControllerInterface;
import controller.UIInterface;
import domain.Company;
import domain.user.CarMechanic;

public class PerformAssemblyTaskHandler implements ControllerInterface {

	private UIInterface ui;
	private Company company;
	private CarMechanic carMechanic;
	
	public PerformAssemblyTaskHandler(UIInterface ui, Company company, CarMechanic carMechanic) {
		this.ui = ui;
		this.company = company;
		this.carMechanic = carMechanic;
	}

	@Override
	public void run() {
		// TODO
	}

}

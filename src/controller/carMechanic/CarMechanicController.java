package controller.carMechanic;

import java.util.ArrayList;

import controller.ControllerInterface;
import controller.UIInterface;
import domain.Company;
import domain.user.CarMechanic;

public class CarMechanicController implements ControllerInterface {

	private UIInterface ui;
	
	private CheckAssemblyLineStatusHandler checkAssemblyLineStatusHandler;
	private PerformAssemblyTaskHandler performAssemblyTaskHandler;
	
	public CarMechanicController(UIInterface ui, Company company, CarMechanic carMechanic) {
		this.ui = ui;
		
		this.checkAssemblyLineStatusHandler = new CheckAssemblyLineStatusHandler(ui, company);
		this.performAssemblyTaskHandler = new PerformAssemblyTaskHandler(ui, company, carMechanic);
	}
	
	@Override
	public void run() {
		ArrayList<String> possibilities = new ArrayList<String>();
		possibilities.add("Perform assembly tasks");
		possibilities.add("Check assembly line status");
		String answer = ui.askWithPossibilities("What do you want to do?", possibilities);
		switch(answer) {
		case "Perform assembly tasks":		this.checkAssemblyLineStatusHandler.run();
											break;
		case "Check assembly line status":	this.performAssemblyTaskHandler.run();
											break;
		}
	}
}

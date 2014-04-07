package controller;

import java.util.ArrayList;

import controller.carMechanic.CarMechanicController;
import controller.customShop.CustomShopController;
import controller.garageHolder.GarageHolderController;
import controller.manager.ManagerController;
import domain.Company;
import domain.InternalFailureException;
import domain.user.CarMechanic;
import domain.user.CustomShopManager;
import domain.user.GarageHolder;
import domain.user.Manager;

public class MainController implements ControllerInterface {

	protected UIInterface ui;

	private CarMechanicController carMechanicController;
	private CustomShopController customShopController;
	private GarageHolderController garageHolderController;
	private ManagerController managerController;

	public MainController(UIInterface ui) {
		this.ui = ui;

		Company company = null;
		try {
			company = new Company();
		} catch (InternalFailureException e2) {
			ui.display("Internal Error");
		}

		CarMechanic carMechanic = new CarMechanic(1);
		CustomShopManager customShopManager = new CustomShopManager(2);
		GarageHolder garageHolder = new GarageHolder(3);
		Manager manager = new Manager(4);

		this.carMechanicController = new CarMechanicController(ui, company, carMechanic);
		this.customShopController = new CustomShopController();
		
		this.garageHolderController = new GarageHolderController(ui, company, garageHolder);
		this.managerController = new ManagerController(ui, company, manager);
	}

	@Override
	public void run() {
		loop: while (true) {
			ArrayList<String> possibilities = new ArrayList<String>();
			possibilities.add("Car mechanic");
			possibilities.add("Custom shop manager");
			possibilities.add("Garage holder");
			possibilities.add("Manager");
			possibilities.add("Exit");
			String answer =ui.askWithPossibilities("Tell us what you are.", possibilities);
			switch(answer) {
			case "Car mechanic":		carMechanicController.run();
										break;
			case "Custom shop manager":	customShopController.run(ui, company, customShopManager);
										break;
			case "Garage holder":		garageHolderController.run();
										break;
			case "Manager":				managerController.run();
										break;
			case "exit":				break loop;
			}
		}
	}

}

package controller.garageHolder;

import controller.MainController;
import controller.UIInterface;
import domain.Company;
import domain.user.GarageHolder;

public class GarageHolderController extends MainController {

	protected Company company;
	protected GarageHolder garageHolder;
	
	public GarageHolderController(UIInterface ui, Company company, GarageHolder garageHolder) {
		super(ui);
		this.company = company;
		this.garageHolder = garageHolder;
	}

	@Override
	public void run() {
		// TODO
	}
}

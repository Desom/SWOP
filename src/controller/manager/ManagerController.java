package controller.manager;

import controller.MainController;
import controller.UIInterface;
import domain.Company;
import domain.user.Manager;

public class ManagerController extends MainController {

	protected Company company;
	protected Manager manager;
	
	public ManagerController(UIInterface ui, Company company, Manager manager) {
		super(ui);
		this.company = company;
		this.manager = manager;
	}

	@Override
	public void run() {
		// TODO
	}
}

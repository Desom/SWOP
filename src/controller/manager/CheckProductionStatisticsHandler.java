package controller.manager;


import controller.UIInterface;
import domain.Company;
import domain.Statistics;
import domain.user.Manager;

public class CheckProductionStatisticsHandler {

	public void run(UIInterface ui, Company company, Manager manager) {
		Statistics view = company.viewStatistics();
		ui.showStatistics(view);
	}
}

package controller.manager;

import java.util.LinkedList;

import controller.UIInterface;
import domain.Company;
import domain.StatisticsView;
import domain.user.Manager;

public class CheckProductionStatisticsHandler {

	public void run(UIInterface ui, Company company, Manager manager) {
		StatisticsView view = company.viewStatistics();
		ui.showStatistics(view);
	}
}

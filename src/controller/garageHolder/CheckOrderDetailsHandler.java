package controller.garageHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Collections;

import controller.UIInterface;
import domain.Company;
import domain.order.CarOrder;
import domain.order.OrderManager;
import domain.user.GarageHolder;

public class CheckOrderDetailsHandler {

	public void run(UIInterface ui, Company company, GarageHolder garageHolder) {
		while (true) {
			// 1. The system presents an overview of the orders placed by the user,
			// divided into two parts. The first part shows a list of pending order,
			// with estimated completion times, and the second part shows a history
			// of completed orders, sorted most recent first.
			OrderManager orderManager = company.getOrderManager();
			ArrayList<CarOrder> pendingOrders = orderManager.getPendingOrders(garageHolder);
			ArrayList<CarOrder> completedOrders = orderManager.getCompletedOrders(garageHolder);

			Comparator<CarOrder> comparator = new Comparator<CarOrder>() {
				@Override
				public int compare(CarOrder order1, CarOrder order2) {
					return order1.getOrderedTime().compareTo(order2.getOrderedTime());
				}
			};
			Collections.sort(pendingOrders, comparator);
			Collections.sort(completedOrders, comparator);
			// TODO ik ging ervan uit dat met "recent first" de ordered time bedoeld werd. Is dat goed?

			ArrayList<Calendar> completionEstimates = new ArrayList<Calendar>();
			for (CarOrder carOrder : pendingOrders)
				completionEstimates.add(orderManager.completionEstimate(carOrder));
			
			// 2. The user indicates the order he wants to check the details for.
			int answer = ui.askForCarOrder(pendingOrders, completedOrders, completionEstimates);
			if (answer == 0)
				return;

			// 3. The system shows the details of the order.
			// 4. The user indicates he is finished viewing the details.
			if (answer < pendingOrders.size())
				ui.displayPendingCarOrderInfo(pendingOrders.get(answer), completionEstimates.get(answer));
			else
				ui.displayCompletedCarOrderInfo(completedOrders.get(answer));

			
		}
	}
}

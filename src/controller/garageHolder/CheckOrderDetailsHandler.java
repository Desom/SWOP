package controller.garageHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Collections;

import controller.UIInterface;
import domain.Company;
import domain.order.Order;
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
			ArrayList<Order> pendingOrders = orderManager.getAllUnfinishedOrders();
			ArrayList<Order> completedOrders = orderManager.getAllCompletedOrders();

			Comparator<Order> comparator = new Comparator<Order>() {
				@Override
				public int compare(Order order1, Order order2) {
					return order1.getOrderedTime().compareTo(order2.getOrderedTime());
				}
			};
			Collections.sort(pendingOrders, comparator);
			Collections.sort(completedOrders, comparator);

			ArrayList<Calendar> completionEstimates = new ArrayList<Calendar>();
			for (Order carOrder : pendingOrders)
				completionEstimates.add(orderManager.completionEstimate(carOrder));
			
			// 2. The user indicates the order he wants to check the details for.
			int answer = ui.askForCarOrder(pendingOrders, completedOrders, completionEstimates);
			if (answer == 0)
				return;
			answer--;
			// 3. The system shows the details of the order.
			// 4. The user indicates he is finished viewing the details.
			if (answer < pendingOrders.size())
				ui.displayPendingCarOrderInfo(pendingOrders.get(answer), completionEstimates.get(answer));
			else
				ui.displayCompletedCarOrderInfo(completedOrders.get(answer));

			
		}
	}
}

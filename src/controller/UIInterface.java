package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import domain.Statistics;
import domain.assembly.assemblyline.status.AssemblyStatusView;
import domain.scheduling.order.Order;

public interface UIInterface {

	void display(String string);

	int askForInteger(String string, int i);
	
	int askForInteger(String string, int i, int j);

	void showAssemblyLineStatus(AssemblyStatusView currentStatus);

	boolean askYesNoQuestion(String string);
	
	void displayPendingOrderInfo(Order pendingOrder, Calendar completionEstimate);
	
	void displayCompletedOrderInfo(Order completedOrder);

	void displayPendingOrders(ArrayList<Integer> tempIdList,
			ArrayList<Calendar> tempCalendarList);

	void displayCompletedOrders(ArrayList<Integer> tempIdList,
			ArrayList<Calendar> tempCalendarList);
	
	int askForOrder(ArrayList<Order> pendingOrders, ArrayList<Order> completedOrders, ArrayList<Calendar> completionEstimates);

	void fillIn(VehicleOrderForm order);

	int askWithPossibilities(String string, Object[] possibilities);
	
	int askWithPossibilitiesWithCancel(String string, Object[] possibilities);

	void display(Object[] displayableObjects);

	String askWithPossibilities(String string, List<String> possibilities);
	
	void showStatistics(Statistics view);
	
	void printException(Exception e);

	void fillIn(SingleTaskOrderForm orderForm);


}

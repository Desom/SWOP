package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import domain.StatisticsView;
import domain.assembly.AssemblyStatusView;
import domain.order.CarOrder;

public interface UIInterface {

	void display(String string);

	int askForInteger(String string, int i);
	
	int askForInteger(String string, int i, int j);

	void showAssemblyLineStatus(AssemblyStatusView currentStatus);

	boolean askYesNoQuestion(String string);
	
	void displayPendingCarOrderInfo(CarOrder pendingOrder, Calendar completionEstimate);
	
	void displayCompletedCarOrderInfo(CarOrder completedOrder);

	void displayPendingCarOrders(ArrayList<Integer> tempIdList,
			ArrayList<Calendar> tempCalendarList);

	void displayCompletedCarOrders(ArrayList<Integer> tempIdList,
			ArrayList<Calendar> tempCalendarList);
	
	int askForCarOrder(ArrayList<CarOrder> pendingOrders, ArrayList<CarOrder> completedOrders, ArrayList<Calendar> completionEstimates);

	void fillIn(OrderForm order);

	int askWithPossibilities(String string, Object[] possibilities);

	void display(Object[] displayableObjects);

	String askWithPossibilities(String string, List<String> possibilities);
	
	void showStatistics(StatisticsView view);
	
	void printException(Exception e);


}

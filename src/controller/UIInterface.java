package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import domain.assembly.AssemblyStatusView;

public interface UIInterface {

	void display(String string);

	int askForInteger(String string, int i);

	void showAssemblyLineStatus(AssemblyStatusView currentStatus);

	boolean askYesNoQuestion(String string);

	void displayPendingCarOrders(ArrayList<Integer> tempIdList,
			ArrayList<Calendar> tempCalendarList);

	void displayCompletedCarOrders(ArrayList<Integer> tempIdList,
			ArrayList<Calendar> tempCalendarList);

	void fillIn(OrderForm order);

	int askWithPossibilities(String string, Object[] objects);

	void display(Object[] aobjectsrray);

	String askWithPossibilities(String string, List<String> possibilities);


}

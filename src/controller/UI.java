package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import domain.assembly.AssemblyStatusView;
import domain.assembly.DoesNotExistException;
import domain.order.CarOrder;
import domain.policies.InvalidConfigurationException;

public class UI implements UIInterface{
	Scanner scan;
	public UI() {
		scan = new Scanner(System.in);
	}
	public void display(String A){
		System.out.println(A);
	}

	public void display(Object[] list) {
		for (Object object : list)
			System.out.println(object.toString());
	}


	public int askForInteger(String question, int lowerBound){
		try {
			System.out.println(question);
			System.out.println("At least: " + lowerBound + ".");
			int input = Integer.parseInt(scan.nextLine());
			while(lowerBound > input){
				System.out.println("This Number is to low.");
				input = Integer.parseInt(scan.nextLine());
			}
			return input;
		}
		catch (NumberFormatException e) {
			System.out.println("This is not a valid answer.");
			return askForInteger(question, lowerBound);
		}
	}
	
	public int askForInteger(String question, int lowerBound, int upperBound){
		try {
			System.out.println(question);
			int input = Integer.parseInt(scan.nextLine());
			while(lowerBound > input || upperBound < input){
				System.out.println("This is not a good number.");
				input = Integer.parseInt(scan.nextLine());
			}
			return input;
		}
		catch (NumberFormatException e) {
			System.out.println("This is not a valid answer.");
			return askForInteger(question, lowerBound);
		}
	}

	public String askWithPossibilities(String question, List<String> possibilities){
		System.out.println(question);
		String possOutput = "";
		int visualInt = 1;
		for(String poss: possibilities){
			possOutput += visualInt;
			possOutput += ". ";
			possOutput += poss;
			possOutput += "\n";
			visualInt++;
		}
		System.out.println(possOutput);

		int input = -1;
		try {
			input = Integer.parseInt(scan.nextLine());
		}
		catch (NumberFormatException e) {
		}
		while(possibilities.size() < input || input < 1){
			System.out.println("Not a possibility. Pick one from the list:");
			System.out.println(possOutput);
			try {
				input = Integer.parseInt(scan.nextLine());
			}
			catch (NumberFormatException e) {
			}
		}
		return possibilities.get(input-1);
	}

	//TODO mss private methode maken die door beide askWith's wordt opgeroepen...
	public int askWithPossibilities(String question, Object[] possibilities){
		System.out.println(question);
		String possOutput = "";
		int visualInt = 1;
		for(Object poss: possibilities){
			possOutput += visualInt + ". ";
			possOutput += poss;
			possOutput += "\n";
			visualInt++;
		}
		System.out.println(possOutput);
		int input = -1;
		try {
			input = Integer.parseInt(scan.nextLine()) - 1;
		}
		catch (NumberFormatException e) {
		}
		while(possibilities.length <= input || input < 0){
			System.out.println("Not a possibility. Pick one from the list:");
			System.out.println(possOutput);
			try {
				input = Integer.parseInt(scan.nextLine()) - 1;
			}
			catch (NumberFormatException e) {
			}

		}
		return input;
	}

	public boolean askYesNoQuestion(String question){
		ArrayList<String> yesNo = new ArrayList<String>();
		yesNo.add("Yes");
		yesNo.add("No");
		String respons = this.askWithPossibilities(question, yesNo );
		if("Yes".equals(respons))
			return true;
		else
			return false;
	}

	public void showAssemblyLineStatus(AssemblyStatusView statusView) {
		try{
			System.out.println(statusView.getHeader());
			System.out.println("---");
			for(int wsID : statusView.getAllWorkstationIds()){
				System.out.println("Workstation " + wsID);
				try{
					int carOrderID = statusView.getCarOrderIdAt(wsID);
					System.out.println("working at CarOrder " + carOrderID);
				}
				catch(NullPointerException exc){
					System.out.println("Not working at a CarOrder");
				}
				for(String task : statusView.getAllTasksAt(wsID)){
					String taskStatus;
					if(statusView.taskIsDoneAt(task, wsID)){
						taskStatus = "finished";
					}
					else{
						taskStatus = "pending";
					}
					System.out.println("Task: " + task + " = " + taskStatus);
				}
			}
			System.out.println();
		}
		catch (DoesNotExistException exc) {
			System.out.println("There is an internal problem : " + exc.getMessage());
		}
	}
	
	public void fillIn(OrderForm order) {
		for(String i: order.getOptionTypes()){
			boolean inOrde = false;
			while(!inOrde )
			try {
				order.addOption(this.askWithPossibilities("Enter your type of "+i+":", order.getPossibleOptionsOfType(i)));
				inOrde = true;
			} catch (InvalidConfigurationException e) {
				this.display(e.getMessage());
			}
		}

	}
	@Override
	public void displayPendingCarOrders(ArrayList<Integer> tempIdList,
			ArrayList<Calendar> tempCalendarList) {
		display("Your pending orders:");
		for(int i =0; i< Math.max(tempIdList.size(), tempCalendarList.size());i++){
			display(tempIdList.get(i) +" will be delivered around:"+ tempCalendarList.get(i).get(Calendar.DAY_OF_WEEK)+" "+tempCalendarList.get(i).get(Calendar.HOUR_OF_DAY) +"h"+tempCalendarList.get(i).get(Calendar.MINUTE));
		}

	}
	@Override
	public void displayCompletedCarOrders(ArrayList<Integer> tempIdList,
			ArrayList<Calendar> tempCalendarList) {
		display("Your completed orders:");
		for(int i =0; i< Math.max(tempIdList.size(), tempCalendarList.size());i++){
			display(tempIdList.get(i) +" is delivered on:"+ tempCalendarList.get(i).get(Calendar.DAY_OF_WEEK)+" "+tempCalendarList.get(i).get(Calendar.HOUR_OF_DAY) +"h"+tempCalendarList.get(i).get(Calendar.MINUTE));
		}

	}

	@Override
	public int askForCarOrder(ArrayList<CarOrder> pendingOrders, ArrayList<CarOrder> completedOrders, ArrayList<Calendar> completionEstimates) {
		int index = 1;
		display("Your pending orders:");
		for(int i =0; i< Math.max(pendingOrders.size(), completionEstimates.size());i++){
			display(index + ". " + pendingOrders.get(i).getCarOrderID() + " will be delivered around:" + completionEstimates.get(i).get(Calendar.DAY_OF_WEEK) + " " + completionEstimates.get(i).get(Calendar.HOUR_OF_DAY) + "h" + completionEstimates.get(i).get(Calendar.MINUTE));
			index++;
		}
		display("Your completed orders:");
		for(CarOrder carOrder : completedOrders){
			display(index + ". " + carOrder.getCarOrderID() + " is delivered on:" + carOrder.getDeliveredTime().get(Calendar.DAY_OF_WEEK) + " "+carOrder.getDeliveredTime().get(Calendar.HOUR_OF_DAY) + "h"+carOrder.getDeliveredTime().get(Calendar.MINUTE));
			index++;
		}
		display("");
		display(0 + ". Exit this view");
		int answer = askForInteger("Please choose one of the numbered options", 0, index - 1);
		return answer;// Returns 0 when the user wants to leave the overwiew
	}
	
	// TODO chain pendingOrder.getCar().getConfiguration().getModel() ok?
	// dubbel checken als alles wel degelijk een clone is
	@Override
	public void displayPendingCarOrderInfo(CarOrder pendingOrder, Calendar completionEstimate) {
		display("Specification:");
		display("- Car model: " + pendingOrder.getCar().getConfiguration().getModel());
		display("- Options: ");
		display(pendingOrder.getCar().getConfiguration().getAllOptions().toArray());
		display("Order time: " + pendingOrder.getOrderedTime());
		display("Estimated deliver time: " + completionEstimate);
		while (true)
			if (askYesNoQuestion("Do you want to go back to the overview?"))
				return;
	}
	@Override
	public void displayCompletedCarOrderInfo(CarOrder completedOrder) {
		display("Specification:");
		display("- Car model: " + completedOrder.getCar().getConfiguration().getModel());
		display("- Options: ");
		display(completedOrder.getCar().getConfiguration().getAllOptions().toArray());
		display("Order time: " + completedOrder.getOrderedTime());
		display("Delivered time: " + completedOrder.getDeliveredTime());
		while (true)
			if (askYesNoQuestion("Do you want to go back to the overview?"))
				return;
	}
}

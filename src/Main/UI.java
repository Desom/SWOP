package Main;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import Assembly.AssemblyStatusView;
import Assembly.DoesNotExistException;
import Order.OrderForm;
import User.UserAccessException;

public class UI {
	Scanner scan;
	public UI(){
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

	public String askWithPossibilities(String question, List<String> possibilities){
		System.out.println(question);
		String possOutput = "( ";
		for(String poss: possibilities){
			possOutput += poss;
			possOutput += " , ";
		}
		possOutput = possOutput.substring(0,possOutput.length()-3);
		possOutput += " )";
		System.out.println(possOutput);
		String input = scan.nextLine();
		while(!possibilities.contains(input)){
			System.out.println("Not a possibility. Pick one from the list:");
			System.out.println(possOutput);
			input = scan.nextLine();
		}
		return input;
	}

	public int askWithPossibilities(String question, Object[] possibilities){
		System.out.println(question);
		String possOutput = "\n";
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

	public void showAssemblyLineStatus(AssemblyStatusView statusView) throws UserAccessException {
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
			order.setOption(this.askWithPossibilities("Geef uw type "+i+" in", order.getPossibleOptionsOfType(i)));
		}

	}
}

package Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

	public String vraag(){
		return scan.nextLine();
	}

	public int askForInteger(String question, int lowerBound){
		System.out.println(question);
		System.out.println("At least: " + lowerBound + ".");
		int input = scan.nextInt();
		while(lowerBound > input){
			System.out.println("This Number is to low.");
			input = scan.nextInt();
		}
		return input;
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
				System.out.println("working at CarOrder " + statusView.getCarOrderIdAt(wsID));
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
		}
		catch (DoesNotExistException exc) {
			System.out.println("There is an internal problem : " + exc.getMessage());
		}
	}
}

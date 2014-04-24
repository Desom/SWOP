package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;

import domain.Statistics;
import domain.assembly.AssemblyStatusView;
import domain.assembly.DoesNotExistException;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.Order;
import domain.order.SingleTaskOrder;
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
				System.out.println("This number is too low.");
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
	
	public GregorianCalendar askForDate(String question) {
		this.display(question);
		int year = this.askForInteger("Enter the year: ", 2014);
		int month =  this.askForInteger("Enter the month in numbers: ", 1, 12) - 1;

		GregorianCalendar date = new GregorianCalendar(year, month, 1);
		int numberOfDays = date.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		date.set(GregorianCalendar.DAY_OF_MONTH, this.askForInteger("Enter the day: ", 1, numberOfDays));
		date.set(GregorianCalendar.HOUR_OF_DAY, this.askForInteger("Enter the hour of the day: ", 1, 23));
		return date;
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
				int carOrderID = statusView.getCarOrderIdAt(wsID);
				if (carOrderID < 0){
					System.out.println("Empty");
				}
				else{
				System.out.println("working at Order " + carOrderID);
				}
				for(OptionType taskType : statusView.getAllTasksAt(wsID)){
					String taskStatus;
					if(statusView.taskIsDoneAt(taskType, wsID)){
						taskStatus = "finished";
					}
					else{
						taskStatus = "pending";
					}
					System.out.println("Task: " + taskType + " = " + taskStatus);
				}
			}
			System.out.println();
		}
		catch (DoesNotExistException exc) {
			System.out.println("There is an internal problem : " + exc.getMessage());
		}
	}

	public void fillIn(CarOrderForm orderForm) {
		ArrayList<OptionType> mandatoryList = new ArrayList<OptionType>();
		ArrayList<OptionType> nonMandatoryList = new ArrayList<OptionType>();
		for(OptionType oType:OptionType.values()){
			if(oType.isMandatory()){
				mandatoryList.add(oType);
			}
			else{
				nonMandatoryList.add(oType);
			}
		}
		for(OptionType oType: mandatoryList){
			boolean inOrde = false;
			while(!inOrde ){
				try {
					List<Option> options = orderForm.getPossibleOptionsOfType(oType);
					int number = this.askWithPossibilities("Enter your type of "+oType+":", options.toArray());
					orderForm.addOption(options.get(number));
					inOrde = true;
				} catch (InvalidConfigurationException e) {
					this.display(e.getMessage());
				}
			}
		}
		for(OptionType oType: nonMandatoryList){
			if(!orderForm.getPossibleOptionsOfType(oType).isEmpty() && askYesNoQuestion("Do you want to add a " + oType.toString() +" to your order?")){
				boolean inOrde = false;
				while(!inOrde ){
					try {
						List<Option> options = orderForm.getPossibleOptionsOfType(oType);
						int number = this.askWithPossibilities("Enter your type of "+oType+":", options.toArray());
						orderForm.addOption(options.get(number));
						inOrde = true;
					} catch (InvalidConfigurationException e) {
						this.display(e.getMessage());
					}
				}
			}
		}
		try {
			orderForm.completeConfiguration();
		} catch (InvalidConfigurationException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void fillIn(SingleTaskOrderForm orderForm){
		ArrayList<OptionType> possibleTypes = new ArrayList<OptionType>();
		for (OptionType type : OptionType.values())
			if (type.isSingleTaskPossible())
				possibleTypes.add(type);
		int answer1 = this.askWithPossibilities("What do you want to order?", possibleTypes.toArray());
		List<Option> possibleOptions = orderForm.getPossibleOptionsOfType(possibleTypes.get(answer1));
		int answer2 = this.askWithPossibilities("Which option do you want to order?", possibleOptions.toArray());
		try {
			orderForm.addOption(possibleOptions.get(answer2));
			orderForm.completeConfiguration();
		} catch (InvalidConfigurationException e) {
			System.out.println(e.getMessage());
		}
		
		this.display("The current time is : " + this.convertCalendarToDate(orderForm.getCurrentTime()));
		GregorianCalendar date = this.askForDate("Set a deadline.");
		while(date.before(orderForm.getCurrentTime())){
			this.display("This date is already in the past. Try again.");
			date = this.askForDate("Set a deadline.");
		}
		orderForm.setDeadline(date);
		
	}
	
	@Override
	public void displayPendingCarOrders(ArrayList<Integer> tempIdList,
			ArrayList<Calendar> tempCalendarList) {
		display("Your pending orders:");
		for(int i =0; i< Math.max(tempIdList.size(), tempCalendarList.size());i++){
			display(tempIdList.get(i) +" will be delivered around:"+ convertCalendarToDate(tempCalendarList.get(i)));
		}

	}
	@Override
	public void displayCompletedCarOrders(ArrayList<Integer> tempIdList,
			ArrayList<Calendar> tempCalendarList) {
		display("Your completed orders:");
		for(int i =0; i< Math.max(tempIdList.size(), tempCalendarList.size());i++){
			display(tempIdList.get(i) +" is delivered on:"+ convertCalendarToDate(tempCalendarList.get(i)));
		}
	}

	@Override
	public int askForCarOrder(ArrayList<Order> pendingOrders, ArrayList<Order> completedOrders, ArrayList<Calendar> completionEstimates) {
		int index = 1;
		display("Your pending orders:");
		for(int i =0; i< Math.max(pendingOrders.size(), completionEstimates.size());i++){
			String type;
			if(pendingOrders.get(i) instanceof SingleTaskOrder){
				type = ":Single task order ";
			}else{
				type = "Car order ";
			}
			display(index + ". " + type + pendingOrders.get(i).getCarOrderID() + " will be delivered around: " + convertCalendarToDate(completionEstimates.get(i)));
			index++;
		}
		display("Your completed orders:");
		for(Order carOrder : completedOrders){
			String type;
			if(carOrder instanceof SingleTaskOrder){
				type = " type: Single task order";
			}else{
				type = " type: Normal order";
			}
			display(index + ". " + carOrder.getCarOrderID() + " is delivered on:" + convertCalendarToDate(carOrder.getDeliveredTime())+ type);
			index++;
		}
		display("");
		display(0 + ". Exit this view");
		int answer = askForInteger("Please choose one of the numbered options", 0, index - 1);
		return answer;// Returns 0 when the user wants to leave the overwiew
	}

	@Override
	public void displayPendingCarOrderInfo(Order pendingOrder, Calendar completionEstimate) {
		display(pendingOrder.getConfiguration());
		display("Order time: " + convertCalendarToDate(pendingOrder.getOrderedTime()));
		display("Estimated deliver time: " + convertCalendarToDate(completionEstimate));
		while (true)
			if (askYesNoQuestion("Do you want to go back to the overview?"))
				return;
	}
	@Override
	public void displayCompletedCarOrderInfo(Order completedOrder) {
		this.display(completedOrder.getConfiguration());
		display("Order time: " + completedOrder.getOrderedTime());
		display("Delivered time: " + completedOrder.getDeliveredTime());
		while (true)
			if (askYesNoQuestion("Do you want to go back to the overview?"))
				return;
	}
	
	private void display(Configuration configuration) {
		display("Specification:");
		display("- Car model: " + configuration.getModel());
		display("- Options: ");
		for (Option option : configuration.getAllOptions())
			display("	- " + option);
	}
	
	@Override
	public void showStatistics(Statistics view) {
		display("Current statistics:");
		display("Average number of cars completed per day: " + view.getAverageCarsPerDay());
		display("Median number of cars completed per day: " + view.getMedianCarsPerDay());

		display("Cars produced yesterday: " + view.getAmountOfCars1DayAgo());
		display("Cars produced 2 days ago: " + view.getAmountOfCars2DaysAgo());

		display("Average delay of all cars that had a delay: " + view.getAverageDelay());
		display("Median delay of all cars that had a delay: " + view.getMedianDelay());

		display("Last delay : " + view.getLastDelay() + " occurred on " + convertCalendarToDate(view.getLastDelayDay()));
		display("Second to last delay : " + view.getSecondToLastDelay() + " occurred on " + convertCalendarToDate(view.getSecondToLastDelayDay()));

		while (true)
			if (askYesNoQuestion("Do you want to go back to the overview?"))
				return;
	}
	@Override
	public void printException(Exception e) {
		display("Internal Error: " + e.getMessage());
	}

	private String convertCalendarToDate(Calendar calendar){
		if(calendar == null) return null;
		return calendar.get(Calendar.DAY_OF_MONTH)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR_OF_DAY)+"h"+calendar.get(Calendar.MINUTE);

	}

}

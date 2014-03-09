package Main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Controller {
	//TODO betere namen voor methodes...
	private UI ui;
	private Company company;

	public void run(){

	}

	public void managerCase(User user) throws UserAccessException{
		AssemblyLine assembly = this.company.getAssemblyLine(user);

		//1. The user indicates he wants to advance the assembly line.
		while(true){
			String actionRequest = "What do you want to do?";
			ArrayList<String> actionPoss = new ArrayList<String>();
			actionPoss.add("advanceLine");
			actionPoss.add("quit");
			
			String action = ui.askWithPossibilities(actionRequest, actionPoss);
			if("advanceLine".equals(action))
				this.advandeLine(user, assembly);
			if("quit".equals(action))
				return;
		}
	}

	private void advandeLine(User user, AssemblyLine assembly) throws UserAccessException{
		while(true){
		//2. The system presents an overview of the current assembly line status,
		//as well as a view of the future assembly line status (as it would be after
		//completing this use case), including pending and finished tasks at each
		//work post.
			returnType currentStatus = assembly.getCurrentAssemblyLineStatus(user); // Of iets dergelijks
			AssemblyStatusView currentStatusView = new AssemblyStatusView("Current assembly line status", currentStatus);
			ui.showAssemblyLineStatus(currentStatusView);
	
			returnType futureStatus = assembly.getFutureAssemblyLineStatus(user); // Of iets dergelijks
			AssemblyStatusView futureStatusView = new AssemblyStatusView("Future assembly line status", currentStatus);
			ui.showAssemblyLineStatus(futureStatusView);
	
		//3. The user confirms the decision to move the assembly line forward,
		//and enters the time that was spent during the current phase (e.g. 45
		//minutes instead of the scheduled hour).
			boolean doAdvance = ui.askYesNoQuestion("Do you want to advance the assembly line?");
			if(!doAdvance){
				return;
			}
			int timeSpent = ui.askForInteger("Give the time spent during the current phase. (minutes)", 0);
		
			try{
			//4. The system moves the assembly line forward one work post according
			//to the scheduling rules.
				assembly.advanceLine(user, timeSpent);	
				
			//5. The system presents an overview of the new assembly line status.
				returnType newCurrentStatus = assembly.getCurrentAssemblyLineStatus(); // Of iets dergelijks
				AssemblyStatusView newCurrentStatusView = new AssemblyStatusView("Current assembly line status", newCurrentStatus);
				ui.showAssemblyLineStatus(newCurrentStatusView);
			}
			catch(CannotAdvanceException cae){
			//4. (a) The assembly line can not be moved forward due to a work post
			//with unfinished tasks.
				
			//5. The system shows a message to the user, indicating which work post(s)
			//are preventing the assembly line from moving forward.
				ui.display(cae.getMessage());
			//6. The use case continues in step 6.
			}
		//6. The user indicates he is done viewing the status.
			boolean repeat = ui.askYesNoQuestion("Do you want to view the new future status?");
			if(!repeat){
				return;
			}
		}
	}

	public void garageHolderCase(User user) throws UserAccessException{
		OrderManager ordermanager=this.company.getOrderManager(user);
		ui.display("Dit zijn uw orders die uw noch heeft staan:");
		for(CarOrder order:ordermanager.getPendingOrders(user)){
			ui.display(""+order.getCarOrderID());
		}
		ui.display("Dit zijn uw orders die al gedaan zijn:");
		for(CarOrder order:ordermanager.getCompletedOrders(user)){
			ui.display(""+order.getCarOrderID());
		}
		String antwoord = "";
		while(!antwoord.equals("V") && !antwoord.equals("N")){
			ui.display("Wilt u de overview (V)erlaten of een (N)ieuwe order plaatsen");
			antwoord = ui.vraag();
		}
		if(antwoord.equals("N")){
			CarModelCatalog catalog = company.getCatalog(user);
			GregorianCalendar calender = ordermanager.placeOrder(new OurOrderform(user,catalog,ui));
			ui.display("Uw  order zou klaar moeten zijn op "+calender.get(Calendar.DAY_OF_MONTH)+"-"+calender.get(Calendar.MONTH)+"-"+calender.get(Calendar.YEAR)+" om "+calender.get(Calendar.HOUR)+"u"+calender.get(Calendar.MINUTE)+".");
		}
	}

	// TODO de car mechanic use case heeft getAllWorkstations nodig, deze is momenteel enkel beschikbaar voor manager
	public void carMechanicCase(User carMechanic){
		ui.askWithPossibilities("Which workstation are you currently residing at?", company.getAllWorkStations(carMechanic).toArray());
		Workstation workstation;
		ui.askWithPossibilities("Which pending task do you want to work on?", workstation.getAllPendingTasks(carMechanic));
		AssemblyTask task;
		workstation.selectTask(carMechanic, task);
		while(true) {
		ui.display(workstation.getActiveTaskInformation(carMechanic).toArray());
		ui.askYesNoQuestion("Please indicate when you have completed the assembly task");
		}
	}
}

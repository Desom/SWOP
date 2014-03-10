package Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Controller {
	private UI ui;
	private Company company;

	public void run(){
		ui = new UI();
		try {
			company = new Company();
		} catch (IOException | CarModelCatalogException e) {
			e.printStackTrace();
		}
		ui.display("Geef aan of uw mechanic, garageholder of manager bent");
		String antwoord =ui.vraag();
		if(antwoord.equals("mechanic")) this.carMechanicCase(new CarMechanic(12345));
		if(antwoord.equals("manager"))
			try {
				this.managerCase(new Manager(12345));
			} catch (UserAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if(antwoord.equals("garageholder"))
			try {
				this.garageHolderCase(new GarageHolder(2));
			} catch (UserAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
				this.actionAdvanceLine(user, assembly);
			if("quit".equals(action))
				return;
		}
	}

	private void actionAdvanceLine(User user, AssemblyLine assembly) throws UserAccessException{
		while(true){
			//2. The system presents an overview of the current assembly line status,
			//as well as a view of the future assembly line status (as it would be after
			//completing this use case), including pending and finished tasks at each
			//work post.
			AssemblyStatusView currentStatus = assembly.currentStatus(user);
			ui.showAssemblyLineStatus(currentStatus);

			AssemblyStatusView futureStatus = assembly.futureStatus(user);
			ui.showAssemblyLineStatus(futureStatus);

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
				AssemblyStatusView newCurrentStatus = assembly.currentStatus(user);
				ui.showAssemblyLineStatus(newCurrentStatus);
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
		for(String order:ordermanager.getPendingOrders(user)){
			ui.display(""+order);
		}
		ui.display("Dit zijn uw orders die al gedaan zijn:");
		for(String order:ordermanager.getCompletedOrders(user)){
			ui.display(""+order);
		}
		String antwoord = "";
		while(!antwoord.equals("V") && !antwoord.equals("N")){
			ui.display("Wilt u de overview (V)erlaten of een (N)ieuwe order plaatsen");
			antwoord = ui.vraag();
		}
		if(antwoord.equals("N")){
			CarModelCatalog catalog = company.getCatalog(user);
			OurOrderform order = new OurOrderform(user,catalog,ui);
			String antwoord2 ="";
			while(!antwoord2.equals("Y") && !antwoord2.equals("N")){
				ui.display("Wilt u de order bevestigen? Y/N");
				antwoord2 = ui.vraag();
			}
			if(antwoord2.equals("Y")){GregorianCalendar calender = ordermanager.placeOrder(order);
			ui.display("Uw  order zou klaar moeten zijn op "+calender.get(Calendar.DAY_OF_MONTH)+"-"+calender.get(Calendar.MONTH)+"-"+calender.get(Calendar.YEAR)+" om "+calender.get(Calendar.HOUR)+"u"+calender.get(Calendar.MINUTE)+".");
			}else{
				this.garageHolderCase(user);
			}
		}
	}

	public void carMechanicCase(User carMechanic) throws UserAccessException{
		//TODO optimaliseren
		int workstationInt = ui.askWithPossibilities("Which workstation are you currently residing at?", company.getAllWorkStations(carMechanic).toArray());
		Workstation workstation = company.getAllWorkStations(carMechanic).get(workstationInt);
		while(true) {
			int taskInt = ui.askWithPossibilities("Which pending task do you want to work on?", workstation.getAllPendingTasks(carMechanic).toArray());
			AssemblyTask task = workstation.getAllPendingTasks(carMechanic).get(taskInt);
			workstation.selectTask(carMechanic, task);
			ui.display(workstation.getActiveTaskInformation(carMechanic).toArray());
			if (ui.askYesNoQuestion("Please indicate when you have completed the assembly task"))
				workstation.completeTask(carMechanic);
			if (!ui.askYesNoQuestion("Do you want to work on a task again?"))
				break;
		}
		ui.display("You are now logged off.\nHave a nice day!");
	}
}

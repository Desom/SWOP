package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import domain.Company;
import domain.InternalFailureException;
import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyStatusView;
import domain.assembly.AssemblyTask;
import domain.assembly.CannotAdvanceException;
import domain.assembly.Workstation;
import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.CarOrder;
import domain.order.OrderManager;
import domain.user.CarMechanic;
import domain.user.GarageHolder;
import domain.user.Manager;
import domain.user.User;

public class Controller {
	private UIInterface ui;
	private Company company;

	public void run(UIInterface ui)  {
		this.ui =ui;


		try {
			company = new Company();
		} catch (InternalFailureException e2) {
			ui.display("Internal Error");
		}
		while (true) {

			ArrayList<String> list = new ArrayList<String>();
			list.add("Car mechanic");
			list.add("Garage holder");
			list.add("Manager");
			list.add("Exit");
			String antwoord =ui.askWithPossibilities("Tell us what you are.", list);
			if(antwoord.equals("Car mechanic"))
				this.carMechanicCase(new CarMechanic(12345));
			if(antwoord.equals("Manager")){
				try {
					this.managerCase(new Manager(12345));
				} catch (InternalFailureException e) {
					ui.display("Internal Error");
				}
			}
			if(antwoord.equals("Garage holder"))
				this.garageHolderCase(new GarageHolder(2));
			if(antwoord.equals("Exit"))
				break;
		}
	}

	private void managerCase(User user) throws InternalFailureException{
		AssemblyLine assembly = this.company.getAssemblyLine();

		//1. The user indicates he wants to advance the assembly line.
		while(true){
			String actionRequest = "What do you want to do?";
			ArrayList<String> actionPoss = new ArrayList<String>();
			actionPoss.add("Advance assembly line");
			actionPoss.add("Quit");

			String action = ui.askWithPossibilities(actionRequest, actionPoss);
			if("Advance assembly line".equals(action))
				this.actionAdvanceLine(user, assembly);
			if("Quit".equals(action)){
				ui.display("You are now logged off.");
				return;
			}
		}
	}

	private void actionAdvanceLine(User user, AssemblyLine assembly) throws InternalFailureException{
		boolean repeat = true;
		while(repeat){

			int timeSpent = ui.askForInteger("Give the time spent during the current phase. 0 if it's the beginning of the day.(minutes)", 0);

			//2. The system presents an overview of the current assembly line status,
			//as well as a view of the future assembly line status (as it would be after
			//completing this use case), including pending and finished tasks at each
			//work post.
			AssemblyStatusView currentStatus = assembly.currentStatus();
			ui.showAssemblyLineStatus(currentStatus);

			AssemblyStatusView futureStatus = assembly.futureStatus(timeSpent);
			ui.showAssemblyLineStatus(futureStatus);

			//3. The user confirms the decision to move the assembly line forward,
			//and enters the time that was spent during the current phase (e.g. 45
			//minutes instead of the scheduled hour).
			boolean doAdvance = ui.askYesNoQuestion("Do you want to advance the assembly line?");
			if(!doAdvance){
				return;
			}

			try{
				//4. The system moves the assembly line forward one work post according
				//to the scheduling rules.
				assembly.advanceLine(timeSpent);	

				//5. The system presents an overview of the new assembly line status.
				AssemblyStatusView newCurrentStatus = assembly.currentStatus();
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
			repeat = ui.askYesNoQuestion("Do you want to view the new future status?");
		}
	}
	

	

	//TODO clone array -> beter clonen
	public void carMechanicCase(CarMechanic carMechanic){
		// 1. The system asks the user what work post he is currently residing at
		LinkedList<Workstation> workstations = company.getAllWorkstations();
		int workstationIndex = ui.askWithPossibilities("Which workstation are you currently residing at?", workstations.toArray().clone());
		Workstation workstation = workstations.get(workstationIndex);
		// 2. The user selects the corresponding work post.
		workstation.addCarMechanic(carMechanic);
		while(true) {
			// 3. The system presents an overview of the pending assembly tasks for the
			// car at the current work post.
			if (workstation.getAllPendingTasks().isEmpty()) {
				ui.display("This workstation has no pending assembly tasks. Please try again later or go to another workstation.");
				break;
			}
			// 4. The user selects one of the assembly tasks.
			ArrayList<AssemblyTask> tasks = workstation.getAllPendingTasks();
			int taskIndex = ui.askWithPossibilities("Which pending task do you want to work on?", tasks.toArray().clone());
			AssemblyTask task = tasks.get(taskIndex);
			workstation.selectTask(task);
			// 5. The system shows the assembly task information, including the
			// sequence of actions to perform.
			ui.display(workstation.getActiveTaskInformation().toArray());
			// 6. The user performs the assembly tasks and indicates when the assembly
			// task is finished.
			//TODO wel vrij stom dat je hier enkel Yes kan zeggen, want No wordt gevolgd door dezelfde vraag.
			while (!ui.askYesNoQuestion("Please indicate if you have completed the assembly task"))
				;
			workstation.completeTask(carMechanic);
			// 8. (a) The user indicates he wants to stop performing assembly tasks
			if (!ui.askYesNoQuestion("Do you want to work on a task again?"))
				break;
			// 7. The system stores the changes and presents an updated overview of
			// pending assembly tasks for the car at the current work post.
			// By restarting the while-loop.
		}
		// 9. The use case ends here.
		workstation.removeCarMechanic();
		ui.display("You are now logged off.");
	}

	
}

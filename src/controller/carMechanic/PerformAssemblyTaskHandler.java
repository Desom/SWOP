package controller.carMechanic;

import java.util.ArrayList;
import java.util.LinkedList;

import controller.UIInterface;
import domain.Company;
import domain.assembly.AssemblyTask;
import domain.assembly.Workstation;
import domain.user.CarMechanic;

public class PerformAssemblyTaskHandler {

	//TODO clone array -> beter clonen (bij workstations en tasks)
	public void run(UIInterface ui, Company company, CarMechanic carMechanic) {
		// 1. The system asks the user what work post he is currently residing at
		LinkedList<Workstation> workstations = company.getAllWorkstations();
		int workstationIndex = ui.askWithPossibilities("Which workstation are you currently residing at?", workstations.toArray().clone());
		Workstation workstation = workstations.get(workstationIndex);
		// 2. The user selects the corresponding work post.
		workstation.addCarMechanic(carMechanic);
		while(true) {
			ArrayList<AssemblyTask> tasks = workstation.getAllPendingTasks();
			if (tasks.isEmpty()) {
				ui.display("This workstation has no pending assembly tasks. Please try again later or go to another workstation.");
				break;
			}
			// 3. The system presents an overview of the pending assembly tasks for the
			// car at the current work post.
			// 4. The user selects one of the assembly tasks.
			int taskIndex = ui.askWithPossibilities("Which pending task do you want to work on?", tasks.toArray().clone());
			AssemblyTask task = tasks.get(taskIndex);
			workstation.selectTask(task);
			// 5. The system shows the assembly task information, including the
			// sequence of actions to perform.
			ui.display(workstation.getActiveTaskInformation().toArray());
			// 6. The user performs the assembly tasks and indicates when the assembly
			// task is finished together with the time it took him to finish
			// the job.
			int minutes = ui.askForInteger("How long did it took you to complete this task? (in minutes)", 0);
			workstation.completeTask(carMechanic, minutes);
			// 7. If all the assembly tasks at the assembly line are finished, the assembly
			// line is shifted automatically and the production schedule is updated.
			// The system presents an updated overview of pending assembly tasks for
			// the car at the current work post.
			ui.display("This is the updated list of pending tasks at this workstation:");
			ui.display(workstation.getAllPendingTasks().toArray().clone());
			// 8. (a) The user indicates he wants to stop performing assembly tasks
			if (!ui.askYesNoQuestion("Do you want to work on a task again?"))
				break;
			// 7. The system stores the changes and presents an updated overview of
			// pending assembly tasks for the car at the current work post.
			// By restarting the while-loop.
		}
		// 9. The use case ends here.
		workstation.removeCarMechanic();
		ui.display("You are removed from " + workstation.toString());
	}

}

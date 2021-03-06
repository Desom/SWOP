package controller.manager;



import controller.UIInterface;
import domain.Company;
import domain.assembly.assemblyline.AssemblyLine;

public class ChangeAssemblyLineStatusHandler {

	public void run(UIInterface ui, Company company) {
		while(true) {
			int answer1 = ui.askWithPossibilitiesWithCancel("Which assembly line do you want to change the status of?", company.getAssemblyLines().toArray());
			if (answer1 == -1)
				return;
			AssemblyLine assemblyLine = company.getAssemblyLines().get(answer1);
			ui.display("Current status: " + assemblyLine.getCurrentStatus());
			int answer2 = ui.askWithPossibilitiesWithCancel("Which status do you want to choose?", assemblyLine.getPossibleStatuses().toArray());
			if (answer2 != -1) {
				assemblyLine.setCurrentStatus(assemblyLine.getPossibleStatuses().get(answer2));
				break;
			}
		}
	}
}

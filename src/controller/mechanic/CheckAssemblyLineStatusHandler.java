package controller.mechanic;

import controller.UIInterface;
import domain.Company;
import domain.assembly.assemblyline.AssemblyLine;

public class CheckAssemblyLineStatusHandler {

	public void run(UIInterface ui, Company company) {
		int assemblyLineIndex = ui.askWithPossibilities("Which assembly line do you want the status of?", company.getAssemblyLines().toArray());
		AssemblyLine assemblyLine = company.getAssemblyLines().get(assemblyLineIndex);
		ui.showAssemblyLineStatus(assemblyLine.getAssemblyLineView());
	}
	
}

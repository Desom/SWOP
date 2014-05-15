package controller.manager;

import java.util.ArrayList;

import controller.UIInterface;
import domain.Company;
import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineStatus;

public class ChangeAssemblyLineStatusHandler {
	
	public void run(UIInterface ui, Company company) {
		ArrayList<AssemblyLineStatus> statuses = new ArrayList<AssemblyLineStatus>();
		for (AssemblyLine line : company.getAssemblyLines())
			statuses.add(line.getCurrentStatus());
		int answer = ui.askWithPossibilities("Which assembly line do you want to change the status of?", statuses.toArray());
		AssemblyLine assemblyLine = company.getAssemblyLines().get(answer);
		
	}

}

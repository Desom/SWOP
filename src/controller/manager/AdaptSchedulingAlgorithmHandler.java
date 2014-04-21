package controller.manager;

import controller.UIInterface;
import domain.Company;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.assembly.algorithm.SpecificationBatchSchedulingAlgorithm;
import domain.user.Manager;

public class AdaptSchedulingAlgorithmHandler {

	public void run(UIInterface ui, Company company, Manager manager) {
//		1. The user wants to select an alternative scheduling algorithm.
		AssemblyLineScheduler assemblyLineScheduler = company.getAssemblyLine().getAssemblyLineScheduler();
//		2. The system shows the available algorithms (3), as well as the currently
//		selected algorithm.
		ui.display("This is the currently selected algorithm:");
		SchedulingAlgorithm[] currentAlgorithm = new SchedulingAlgorithm[1];
		currentAlgorithm[0] = assemblyLineScheduler.getCurrentAlgorithm();
		ui.display(currentAlgorithm);
		
//		3. The user selects the new scheduling algorithm to be used.
		Object[] possibilities = assemblyLineScheduler.getPossibleAlgorithms().toArray();
		int answer = ui.askWithPossibilities("Select one of the available algorithms.", possibilities);

		
//		4. The system applies the new scheduling algorithm and updates its
//		state accordingly.
		if(possibilities[answer] instanceof SpecificationBatchSchedulingAlgorithm){
			SpecificationBatchSchedulingAlgorithm algorithm = (SpecificationBatchSchedulingAlgorithm) possibilities[answer];
//			3. (a) The user indicates he wants to use the Specication Batch algorithm.
//			4. The system shows a list of the sets of car options for which more
//			than 3 orders are pending in the production queue.
//			5. The user selects one of these sets for batch processing
//			6. The use case continues in step 4.
		}
		
		
//		2. (a) The user indicates he wants to cancel selecting a new scheduling
//		algorithm.
//		3. The use case ends here.



	}
}

package controller.manager;

import java.util.ArrayList;

import controller.UIInterface;
import domain.Company;
import domain.configuration.Configuration;
import domain.scheduling.schedulers.FactoryScheduler;
import domain.scheduling.schedulers.algorithm.BasicSchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.FactorySchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.SchedulingAlgorithm;
import domain.scheduling.schedulers.algorithm.SpecificationBatchSchedulingAlgorithm;
import domain.user.Manager;

public class AdaptSchedulingAlgorithmHandler {

	public void run(UIInterface ui, Company company, Manager manager) {


		//		1. The user wants to select an alternative scheduling algorithm.
		FactoryScheduler factoryScheduler = company.getFactoryScheduler();

		//		2. The system shows the currently selected algorithm.
		ui.display("This is the currently selected algorithm:");
		FactorySchedulingAlgorithm[] currentAlgorithm = new FactorySchedulingAlgorithm[1];
		currentAlgorithm[0] = factoryScheduler.getCurrentAlgorithm();
		ui.display(currentAlgorithm);

		if(!ui.askYesNoQuestion("Do you want to change the algorithm used?")){
			//		2. (a) The user indicates he wants to cancel selecting a new scheduling
			//		algorithm.
			//		3. The use case ends here.
			return;
		}

		//		2. The system shows the available algorithms, as well as the currently
		//		selected algorithm.
		//		3. The user selects the new scheduling algorithm to be used.
		Object[] possibilities = factoryScheduler.getPossibleAlgorithms().toArray();
		int answer2 = ui.askWithPossibilities("Select one of the available algorithms.", possibilities);

		FactorySchedulingAlgorithm chosenAlgorithm = (FactorySchedulingAlgorithm) possibilities[answer2];

		SchedulingAlgorithm innerChosenAlgorithm = null;
		if(chosenAlgorithm instanceof BasicSchedulingAlgorithm){
			innerChosenAlgorithm = ((BasicSchedulingAlgorithm) chosenAlgorithm).getInnerAlgorithm();
		}

		if(innerChosenAlgorithm instanceof SpecificationBatchSchedulingAlgorithm){
			SpecificationBatchSchedulingAlgorithm specBatch = (SpecificationBatchSchedulingAlgorithm) innerChosenAlgorithm;
			//			3. (a) The user indicates he wants to use the Specication Batch algorithm.
			//			4. The system shows a list of the sets of vehicle options for which more
			//			than 3 orders are pending in the production queue.
			//			5. The user selects one of these sets for batch processing
			ArrayList<Configuration> possibleBatch = specBatch.searchForBatchConfiguration(factoryScheduler);
			if(possibleBatch.isEmpty()){
				ui.display("There are no configurations that can be batched.");
				return;
			}
			int answer3 = ui.askWithPossibilities("Choose a configuration that needs to be produced in batch.", possibleBatch.toArray());

			specBatch.setConfiguration(possibleBatch.get(answer3));

			//			6. The use case continues in step 4.
		}

		//		4. The system applies the new scheduling algorithm and updates its
		//		state accordingly.

		factoryScheduler.setSchedulingAlgorithm((FactorySchedulingAlgorithm) possibilities[answer2]);



	}
}

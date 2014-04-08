package controller.customShop;

import java.util.ArrayList;

import controller.UIInterface;
import domain.Company;
import domain.assembly.AssemblyTask;
import domain.configuration.OptionType;
import domain.user.CustomShopManager;

public class OrderSingleTaskHandler {

	// TODO niet af
	public void run(UIInterface ui, Company company, CustomShopManager customShopManager) {
		// 1. The user wants to order a single task.
		// 2. The system shows a list of available tasks.
		// 3. The user selects the task he wants to order.
		// 4. The system asks the user for a deadline, as well as the required task options (e.g. Color).
		
		// 5. The user enters the required details.
		
		// 6. The system stores the new order and updates the production schedule.
		
		// 7. The system presents an estimated completion date for the new order.
		
		
		// 5. (a) The user indicates he wants to cancel placing an order.
		
		// 6. The use case returns to step 1.
	}
	
	private ArrayList<OptionType> getAvailableSingleTastOptionTypes() {
		ArrayList<OptionType> singleTaskOptionTypes = new ArrayList<OptionType>();
		for (OptionType optionType : OptionType.values())
			if (optionType.isSingleTaskPossible())
				singleTaskOptionTypes.add(optionType);
		return singleTaskOptionTypes;
	}
}

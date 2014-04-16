package controller.customShop;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import controller.OrderForm;
import controller.OurOrderform;
import controller.UIInterface;
import domain.Company;
import domain.configuration.OptionType;
import domain.order.OrderManager;
import domain.order.SingleTaskOrder;
import domain.user.CustomShopManager;

public class OrderSingleTaskHandler {

	public void run(UIInterface ui, Company company, CustomShopManager customShopManager) {
		// 1. The user wants to order a single task.
		// 2. The system shows a list of available tasks.
		OrderManager orderManager = company.getOrderManager();
		// 3. The user selects the task he wants to order.
		int i=-1;
		ArrayList<OptionType> soortenOpties = this.getAvailableSingleTastOptionTypes();
		while( i-1 < soortenOpties.size() && i-1>=0  )
		 i=ui.askWithPossibilities("Which kind of task has to be executed?", this.getAvailableSingleTastOptionTypes().toArray());
		// 4. The system asks the user for a deadline, as well as the required task options (e.g. Color).
		OrderForm orderForm = new OurOrderform(null,orderManager.getSingleTaskOrderPolicies());
		
		// 5. The user enters the required details.
		GregorianCalendar deadline = ui.fillInSingleTaskOrder(orderForm);
		
		
		boolean antwoord = ui.askYesNoQuestion("Do you want to confirm this order?");
		if(antwoord){
			// 6. The system stores the new order and updates the production schedule.
			SingleTaskOrder order=orderManager.placeSingleTaskOrder(customShopManager,orderForm.getConfiguration(), deadline );
			// 7. The system presents an estimated completion date for the new order.
			GregorianCalendar calender = orderManager.completionEstimate(order);
			String time = getTime(calender);
			ui.display("Your order should be ready at "+ time+".");
		}else{
			/// 5. (a) The user indicates he wants to cancel placing an order.
			
			// 6. The use case returns to step 1.
			this.run(ui, company, customShopManager);
		}
	}
		
	private String getTime(GregorianCalendar calender) {
		String date= calender.get(Calendar.DAY_OF_MONTH)+"-"+calender.get(Calendar.MONTH)+"-"+calender.get(Calendar.YEAR)+" at "+calender.get(Calendar.HOUR_OF_DAY)+"h"+calender.get(Calendar.MINUTE);
		return date;
	}
	
	private ArrayList<OptionType> getAvailableSingleTastOptionTypes() {
		ArrayList<OptionType> singleTaskOptionTypes = new ArrayList<OptionType>();
		for (OptionType optionType : OptionType.values())
			if (optionType.isSingleTaskPossible())
				singleTaskOptionTypes.add(optionType);
		return singleTaskOptionTypes;
	}
}

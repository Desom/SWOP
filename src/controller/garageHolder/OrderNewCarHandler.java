package controller.garageHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import controller.CommunicationTool;
import controller.CarOrderForm;
import controller.UIInterface;
import domain.Company;
import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.Order;
import domain.order.OrderManager;
import domain.user.GarageHolder;

public class OrderNewCarHandler implements CommunicationTool{



	private Company company;


	public void run(UIInterface ui, Company company, GarageHolder garageHolder) {
		this.company = company;
		OrderManager ordermanager= company.getOrderManager();
		//1.The system presents an overview of the orders placed by the user,
		//divided into two parts. The first part shows a list of pending orders,
		//with estimated completion times.
		ArrayList<Integer> tempIdList= new ArrayList<Integer>();
		ArrayList<Calendar> tempCalendarList= new ArrayList<Calendar>();
		for(Order order:ordermanager.getPendingOrders(garageHolder)){
			tempIdList.add(order.getUserId());
			tempCalendarList.add(ordermanager.completionEstimate(order));
		}
		ui.displayPendingCarOrders(tempIdList, tempCalendarList);
		tempIdList= new ArrayList<Integer>();
		tempCalendarList= new ArrayList<Calendar>();
		//1.the second part shows a history
		//of completed orders, sorted most recent first.
		ArrayList<Order> orders = getSortedCompletedOrder(garageHolder, ordermanager);
		for(Order order:orders){
			tempIdList.add(order.getUserId());
			tempCalendarList.add(order.getDeliveredTime());
		}
		ui.displayCompletedCarOrders(tempIdList, tempCalendarList);
		//2.The user indicates he wants to place a new car order.

		ArrayList<String> list = new ArrayList<String>();
		list.add("Leave");
		list.add("Place a new order");
		String antwoord = ui.askWithPossibilities("Do you want to leave this overview or place a new order?",list);

		//3. The system shows a list of available car models
		//4. The user indicates the car model he wishes to order.
		if(antwoord.equals("Place a new order")){
			CarModelCatalog catalog = company.getCatalog();
			CarModel model = null;
			while(model == null ){
				ArrayList<CarModel> modelList = new ArrayList<CarModel>();
				for(CarModel carM: catalog.getAllModels()){
					modelList.add(carM);
				}
				int modelInt = ui.askWithPossibilities("Please input your car model", modelList.toArray());
				model = modelList.get(modelInt);			
			}
			//5. The system displays the ordering form.
			//6. The user completes the ordering form.
			CarOrderForm orderForm = new CarOrderForm(model, ordermanager.getCarOrderPolicies());
			ui.fillIn(orderForm);
			
			while(!orderForm.getConfiguration().isCompleted()){
				orderForm = new CarOrderForm(model, ordermanager.getCarOrderPolicies());
				ui.fillIn(orderForm);
			}
			
			boolean antwoord2 = ui.askYesNoQuestion("Do you want to confirm this order?");
			if(antwoord2){
				//7. The system stores the new order and updates the production schedule.
				//8. The system presents an estimated completion date for the new order.
				GregorianCalendar calender = ordermanager.completionEstimate(ordermanager.placeCarOrder(garageHolder, orderForm.getConfiguration()));
				String time = getTime(calender);
				ui.display("Your order should be ready at "+ time+".");
			}else{
				//6. (a) The user indicates he wants to cancel placing the order.
				//7. The use case returns to step 1.
				this.run(ui, company, garageHolder);
			}
		}
		//1. (a) The user indicates he wants to leave the overview.
		//2. The use case ends here.
	}

	/**
	 * Get a car model based on the name
	 * @param name the name
	 * @return a car model based with the name name
	 * 	       null if the name does not match a model 
	 */
	private CarModel getCarModel(String name, Company company){
		CarModelCatalog catalog = company.getCatalog();
		for(CarModel possible: catalog.getAllModels()){
			if(possible.getName().equals(name)) return possible;
		}
		return null;
	}


	private String getTime(GregorianCalendar calender) {
		String date= calender.get(Calendar.DAY_OF_MONTH)+"-"+(calender.get(Calendar.MONTH)+1)+"-"+calender.get(Calendar.YEAR)+" at "+calender.get(Calendar.HOUR_OF_DAY)+"h"+calender.get(Calendar.MINUTE);
		return date;
	}

	private ArrayList<Order> getSortedCompletedOrder(GarageHolder user, OrderManager ordermanager)  {
		ArrayList<Order> Orders = ordermanager.getCompletedOrders(user);
		ArrayList<Order> result = new ArrayList<Order>();
		while(!Orders.isEmpty()){
			Order min = Orders.get(0);
			for(int i=1; i < Orders.size(); i++){
				if(ordermanager.completionEstimate(Orders.get(i)).before(ordermanager.completionEstimate(min))){
					min = Orders.get(i);
				}
			}
			Orders.remove(min);
			result.add(min);
		}
		return result;
	}
	public List<String> getOptionTypes() {
		ArrayList<String> result = new ArrayList<String>();
		for(OptionType i:OptionType.values()) result.add(i.toString());
		return result;
	}


	/**
	 * Get a car option based on the description
	 * @param description the description
	 * @param company 
	 * @return a car option based with the description description
	 *         null if the description does not match an option
	 */
	public Option getOption(String description){
		CarModelCatalog catalog = company.getCatalog();
		for(Option possible: catalog.getAllOptions()){
			if(possible.getDescription().equals(description)) return possible;
		}
		return null;
	}
}

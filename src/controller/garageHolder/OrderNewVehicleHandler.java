package controller.garageHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import controller.VehicleOrderForm;
import controller.UIInterface;
import domain.Company;
import domain.configuration.VehicleCatalog;
import domain.configuration.models.VehicleModel;
import domain.scheduling.order.Order;
import domain.scheduling.order.OrderManager;
import domain.user.GarageHolder;

public class OrderNewVehicleHandler{

	public void run(UIInterface ui, Company company, GarageHolder garageHolder) {
		OrderManager ordermanager= company.getOrderManager();
		//1.The system presents an overview of the orders placed by the user,
		//divided into two parts. The first part shows a list of pending orders,
		//with estimated completion times.
		ArrayList<Integer> tempIdList= new ArrayList<Integer>();
		ArrayList<Calendar> tempCalendarList= new ArrayList<Calendar>();
		for(Order order:ordermanager.getPendingOrders(garageHolder)){
			tempIdList.add(order.getOrderID());
			tempCalendarList.add(ordermanager.completionEstimate(order));
		}
		ui.displayPendingOrders(tempIdList, tempCalendarList);
		tempIdList= new ArrayList<Integer>();
		tempCalendarList= new ArrayList<Calendar>();
		//1.the second part shows a history
		//of completed orders, sorted most recent first.
		ArrayList<Order> orders = getSortedCompletedOrder(garageHolder, ordermanager);
		for(Order order:orders){
			tempIdList.add(order.getUserId());
			tempCalendarList.add(order.getDeliveredTime());
		}
		ui.displayCompletedOrders(tempIdList, tempCalendarList);
		//2.The user indicates he wants to place a new vehicle order.

		ArrayList<String> list = new ArrayList<String>();
		list.add("Leave");
		list.add("Place a new order");
		String antwoord = ui.askWithPossibilities("Do you want to leave this overview or place a new order?",list);

		//3. The system shows a list of available vehicle models
		//4. The user indicates the vehicle model he wishes to order.
		if(antwoord.equals("Place a new order")){
			VehicleCatalog catalog = company.getCatalog();
			VehicleModel model = null;
			while(model == null ){
				List<VehicleModel> modelList = catalog.getAllModels();
				int modelInt = ui.askWithPossibilities("Please input your vehicle model", modelList.toArray());
				model = modelList.get(modelInt);			
			}
			//5. The system displays the ordering form.
			//6. The user completes the ordering form.
			VehicleOrderForm orderForm = new VehicleOrderForm(model, ordermanager.getVehicleOrderPolicies());
			ui.fillIn(orderForm);
			
			while(!orderForm.getConfiguration().isCompleted()){
				orderForm = new VehicleOrderForm(model, ordermanager.getVehicleOrderPolicies());
				ui.fillIn(orderForm);
			}
			
			boolean antwoord2 = ui.askYesNoQuestion("Do you want to confirm this order?");
			if(antwoord2){
				//7. The system stores the new order and updates the production schedule.
				//8. The system presents an estimated completion date for the new order.
				GregorianCalendar calender = ordermanager.completionEstimate(ordermanager.placeVehicleOrder(garageHolder, orderForm.getConfiguration()));
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


	private String getTime(GregorianCalendar calender) {
		String date= calender.get(Calendar.DAY_OF_MONTH)+"-"+(calender.get(Calendar.MONTH)+1)+"-"+calender.get(Calendar.YEAR)+" at "+calender.get(Calendar.HOUR_OF_DAY)+"h"+calender.get(Calendar.MINUTE);
		return date;
	}

	private ArrayList<Order> getSortedCompletedOrder(GarageHolder user, OrderManager ordermanager)  {
		ArrayList<Order> orders = ordermanager.getCompletedOrders(user);
		ArrayList<Order> result = new ArrayList<Order>();
		while(!orders.isEmpty()){
			Order min = orders.get(0);
			for(int i=1; i < orders.size(); i++){
				if(orders.get(i).getDeliveredTime().before(min.getDeliveredTime())){
					min = orders.get(i);
				}
			}
			orders.remove(min);
			result.add(min);
		}
		return result;
	}
}

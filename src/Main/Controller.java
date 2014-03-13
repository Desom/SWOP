package Main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import Assembly.AssemblyLine;
import Assembly.AssemblyStatusView;
import Assembly.AssemblyTask;
import Assembly.CannotAdvanceException;
import Assembly.Workstation;
import Car.CarModel;
import Car.CarOrder;
import Order.CarModelCatalog;
import Order.OrderManager;
import Order.OurOrderform;
import User.CarMechanic;
import User.GarageHolder;
import User.Manager;
import User.User;
import User.UserAccessException;

public class Controller {
	private UI ui;
	private Company company;

	public void run()  {
		ui = new UI();

		// TODO

		try {
			company = new Company();
		} catch (InternalFailureException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (true) {

			ArrayList<String> list = new ArrayList<String>();
			list.add("mechanic");
			list.add("garageholder");
			list.add("manager");
			list.add("exit");
			String antwoord =ui.askWithPossibilities("Geef aan of u mechanic, garageholder of manager bent", list);
			if(antwoord.equals("mechanic"))
				try {
					this.carMechanicCase(new CarMechanic(12345));
				} catch (UserAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			if(antwoord.equals("manager"))
				try {
					this.managerCase(new Manager(12345));
				} catch (UserAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InternalFailureException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(antwoord.equals("garageholder"))
				try {
					this.garageHolderCase(new GarageHolder(2));
				} catch (UserAccessException e) {
					ui.display("Er is een fout opgeloden in ons programma gelive ons te verontschuldigen");
				}
			if(antwoord.equals("exit"))
				break;
		}
	}

	public void managerCase(User user) throws UserAccessException, InternalFailureException{
		AssemblyLine assembly = this.company.getAssemblyLine(user);

		//1. The user indicates he wants to advance the assembly line.
		while(true){
			String actionRequest = "What do you want to do?";
			ArrayList<String> actionPoss = new ArrayList<String>();
			actionPoss.add("advanceLine");
			actionPoss.add("quit");

			String action = ui.askWithPossibilities(actionRequest, actionPoss);
			if("advanceLine".equals(action))
				this.actionAdvanceLine(user, assembly);
			if("quit".equals(action)){
				ui.display("You are now logged off.");
				return;
			}
		}
	}

	private void actionAdvanceLine(User user, AssemblyLine assembly) throws UserAccessException, InternalFailureException{
		boolean repeat = true;
		while(repeat){

			int timeSpent = ui.askForInteger("Give the time spent during the current phase. (minutes)", 0);
			
			//2. The system presents an overview of the current assembly line status,
			//as well as a view of the future assembly line status (as it would be after
			//completing this use case), including pending and finished tasks at each
			//work post.
			AssemblyStatusView currentStatus = assembly.currentStatus(user);
			ui.showAssemblyLineStatus(currentStatus);

			AssemblyStatusView futureStatus = assembly.futureStatus(user, timeSpent);
			ui.showAssemblyLineStatus(futureStatus);

			//3. The user confirms the decision to move the assembly line forward,
			//and enters the time that was spent during the current phase (e.g. 45
			//minutes instead of the scheduled hour).
			boolean doAdvance = ui.askYesNoQuestion("Do you want to advance the assembly line?");
			if(!doAdvance){
				return;
			}

			try{
				//4. The system moves the assembly line forward one work post according
				//to the scheduling rules.
				assembly.advanceLine(user, timeSpent);	

				//5. The system presents an overview of the new assembly line status.
				AssemblyStatusView newCurrentStatus = assembly.currentStatus(user);
				ui.showAssemblyLineStatus(newCurrentStatus);
			}
			catch(CannotAdvanceException cae){
				//4. (a) The assembly line can not be moved forward due to a work post
				//with unfinished tasks.

				//5. The system shows a message to the user, indicating which work post(s)
				//are preventing the assembly line from moving forward.
				ui.display(cae.getMessage());
				//6. The use case continues in step 6.
			}
			//6. The user indicates he is done viewing the status.
			repeat = ui.askYesNoQuestion("Do you want to view the new future status?");
		}
	}
	/**
	 * The use case of the garageholder: ordering a car
	 * @param user The user that wants to use the garage holder use case
	 * @throws UserAccessException
	 */
	public void garageHolderCase(User user) throws UserAccessException {
		OrderManager ordermanager=this.company.getOrderManager(user);
		//1.The system presents an overview of the orders placed by the user,
		//divided into two parts. The first part shows a list of pending orders,
		//with estimated completion times.
		ui.display("These are your pending orders:");
		for(CarOrder order:ordermanager.getPendingOrders(user)){
			ui.display("order: "+order.getCarOrderID()+" delivered on:"+getTime(ordermanager.completionEstimate(user, order)));
		}
		//1.the second part shows a history
		//of completed orders, sorted most recent first.
		ui.display("These are you completed order:");
		ArrayList<CarOrder> orders = getSortedCompletedOrder(user, ordermanager);
		for(CarOrder order:orders){
			ui.display(""+order.getCarOrderID()+" "+getTime(order.getDeliveredTime()));
		}
		//2.The user indicates he wants to place a new car order.
		String antwoord = "";
		while(!antwoord.equals("L") && !antwoord.equals("P")){
			ArrayList<String> list = new ArrayList<String>();
			list.add("L");
			list.add("P");
			antwoord = ui.askWithPossibilities("Do you want to (L)eave this overview or (P)lace a new order?",list);
		}
		//3. The system shows a list of available car models
		//4. The user indicates the car model he wishes to order.
		if(antwoord.equals("P")){
			CarModelCatalog catalog = company.getCatalog(user);
			CarModel model = null;
			while(model == null ){
				ArrayList<String> modelList = new ArrayList<String>();
				for(CarModel j: catalog.getAllModels()){
					modelList.add(j.getName());
				}
				String modelname = ui.askWithPossibilities("Please input your car model", modelList);
				model = catalog.getCarModel(modelname);
			}
			//5. The system displays the ordering form.
			//6. The user completes the ordering form.
			OurOrderform order = new OurOrderform(user, model ,catalog);
			ui.fillIn(order);
			String antwoord2 ="";
			while(!antwoord2.equals("Y") && !antwoord2.equals("N")){
				ArrayList<String> list = new ArrayList<String>();
				list.add("Y");
				list.add("N");
				antwoord2 = ui.askWithPossibilities("Do you want to confirm this order? Y/N",list);
			}
			if(antwoord2.equals("Y")){
				//7. The system stores the new order and updates the production schedule.
				//8. The system presents an estimated completion date for the new order.
				GregorianCalendar calender = ordermanager.completionEstimate(user, ordermanager.placeOrder(order));
				String time = getTime(calender);
				ui.display("Your order should be ready at "+ time+".");
			}else{
				//6. (a) The user indicates he wants to cancel placing the order.
				//7. The use case returns to step 1.
				this.garageHolderCase(user);
			}
		}
		//1. (a) The user indicates he wants to leave the overview.
		//2. The use case ends here.
	}

	private String getTime(GregorianCalendar calender) {
		String date= calender.get(Calendar.DAY_OF_MONTH)+"-"+calender.get(Calendar.MONTH)+"-"+calender.get(Calendar.YEAR)+" at "+calender.get(Calendar.HOUR)+"u"+calender.get(Calendar.MINUTE);
		return date;
	}

	private ArrayList<CarOrder> getSortedCompletedOrder(User user, OrderManager ordermanager) throws UserAccessException {
		ArrayList<CarOrder> Orders = ordermanager.getCompletedOrders(user);
		ArrayList<CarOrder> result = new ArrayList<CarOrder>();
		while(!Orders.isEmpty()){
			CarOrder min = Orders.get(0);
			for(int i=1; i < Orders.size(); i++){
				if(ordermanager.completionEstimate(user, Orders.get(i)).before(ordermanager.completionEstimate(user, min))){
					min = Orders.get(i);
				}
			}
			Orders.remove(min);
			result.add(min);
		}
		return result;
	}

	public void carMechanicCase(User carMechanic) throws UserAccessException{
		// 1. The system asks the user what work post he is currently residing at
		LinkedList<Workstation> workstations = company.getAllWorkstations(carMechanic);
		int workstationInt = ui.askWithPossibilities("Which workstation are you currently residing at?", workstations.toArray().clone());
		// 2. The user selects the corresponding work post.
		Workstation workstation = workstations.get(workstationInt);
		workstation.addCarMechanic(carMechanic); //TODO catch error of niet?
		while(true) {
			// 3. The system presents an overview of the pending assembly tasks for the
			// car at the current work post.
			if (workstation.getAllPendingTasks(carMechanic).isEmpty()) {
				ui.display("This workstation has no pending assembly tasks. Please try again later or go to another workstation.");
				break;
			}
			ArrayList<AssemblyTask> tasks = workstation.getAllPendingTasks(carMechanic);
			int taskInt = ui.askWithPossibilities("Which pending task do you want to work on?", tasks.toArray().clone());
			// 4. The user selects one of the assembly tasks.
			AssemblyTask task = tasks.get(taskInt);
			workstation.selectTask(carMechanic, task);
			// 5. The system shows the assembly task information, including the
			// sequence of actions to perform.
			ui.display(workstation.getActiveTaskInformation(carMechanic).toArray());
			// 6. The user performs the assembly tasks and indicates when the assembly
			// task is finished.
			while (!ui.askYesNoQuestion("Please indicate if you have completed the assembly task"))
				;
			workstation.completeTask(carMechanic);
			// 8. (a) The user indicates he wants to stop performing assembly tasks
			if (!ui.askYesNoQuestion("Do you want to work on a task again?"))
				break;
			// 7. The system stores the changes and presents an updated overview of
			// pending assembly tasks for the car at the current work post.
			// By restarting the while-loop.
		}
		// 9. The use case ends here.
		ui.display("You are now logged off.");
	}
}

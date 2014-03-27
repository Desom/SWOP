package Main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import Assembly.AssemblyLine;
import Assembly.AssemblyStatusView;
import Assembly.AssemblyTask;
import Assembly.CannotAdvanceException;
import Assembly.Workstation;
import Car.CarModel;
import Car.CarOrder;
import Car.Option;
import Car.OptionType;
import Order.CarModelCatalog;
import Order.OrderForm;
import Order.OrderManager;
import Order.OurOrderform;
import User.CarMechanic;
import User.GarageHolder;
import User.Manager;
import User.User;

public class Controller implements CommunicationTool{
	private UIInterface ui;
	private Company company;

	public void run(UIInterface ui)  {
		this.ui =ui;


		try {
			company = new Company();
		} catch (InternalFailureException e2) {
			ui.display("Internal Error");
		}
		while (true) {

			ArrayList<String> list = new ArrayList<String>();
			list.add("mechanic");
			list.add("garageholder");
			list.add("manager");
			list.add("exit");
			String antwoord =ui.askWithPossibilities("Tell us what you are.", list);
			if(antwoord.equals("mechanic"))
				this.carMechanicCase(new CarMechanic(12345));
			if(antwoord.equals("manager")){
				try {
					this.managerCase(new Manager(12345));
				} catch (InternalFailureException e) {
					ui.display("Internal Error");
				}
			}
			if(antwoord.equals("garageholder"))
				this.garageHolderCase(new GarageHolder(2));
			if(antwoord.equals("exit"))
				break;
		}
	}

	private void managerCase(User user) throws InternalFailureException{
		AssemblyLine assembly = this.company.getAssemblyLine();

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

	private void actionAdvanceLine(User user, AssemblyLine assembly) throws InternalFailureException{
		boolean repeat = true;
		while(repeat){

			int timeSpent = ui.askForInteger("Give the time spent during the current phase. 0 if it's the begin of the day.(minutes)", 0);

			//2. The system presents an overview of the current assembly line status,
			//as well as a view of the future assembly line status (as it would be after
			//completing this use case), including pending and finished tasks at each
			//work post.
			AssemblyStatusView currentStatus = assembly.currentStatus();
			ui.showAssemblyLineStatus(currentStatus);

			AssemblyStatusView futureStatus = assembly.futureStatus(timeSpent);
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
				assembly.advanceLine(timeSpent);	

				//5. The system presents an overview of the new assembly line status.
				AssemblyStatusView newCurrentStatus = assembly.currentStatus();
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
	private void garageHolderCase(GarageHolder user) {
		OrderManager ordermanager=this.company.getOrderManager();
		//1.The system presents an overview of the orders placed by the user,
		//divided into two parts. The first part shows a list of pending orders,
		//with estimated completion times.
		ArrayList<Integer> tempIdList= new ArrayList<Integer>();
		ArrayList<Calendar> tempCalendarList= new ArrayList<Calendar>();
		for(CarOrder order:ordermanager.getPendingOrders(user)){
			tempIdList.add(order.getUserId());
			tempCalendarList.add(ordermanager.completionEstimate(order));
		}
		ui.displayPendingCarOrders(tempIdList, tempCalendarList);
		tempIdList= new ArrayList<Integer>();
		tempCalendarList= new ArrayList<Calendar>();
		//1.the second part shows a history
		//of completed orders, sorted most recent first.
		ArrayList<CarOrder> orders = getSortedCompletedOrder(user, ordermanager);
		for(CarOrder order:orders){
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
				ArrayList<String> modelList = new ArrayList<String>();
				for(CarModel j: catalog.getAllModels()){
					modelList.add(j.getName());
				}
				String modelname = ui.askWithPossibilities("Please input your car model", modelList);
				model =getCarModel(modelname);
			}
			//5. The system displays the ordering form.
			//6. The user completes the ordering form.
			OurOrderform order = new OurOrderform( model.getName(), this);
			ui.fillIn(order);
			boolean antwoord2 = ui.askYesNoQuestion("Do you want to confirm this order?");
			if(antwoord2){
				//7. The system stores the new order and updates the production schedule.
				//8. The system presents an estimated completion date for the new order.
				GregorianCalendar calender = ordermanager.completionEstimate(ordermanager.placeOrder(user, model, getOptions(order.getOptions())));
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

	private ArrayList<Option> getOptions(ArrayList<String> options) {
		ArrayList<Option> result = new ArrayList<Option>();
		for(String i:options) result.add(getOption(i));
		return result;
	}

	private String getTime(GregorianCalendar calender) {
		String date= calender.get(Calendar.DAY_OF_MONTH)+"-"+calender.get(Calendar.MONTH)+"-"+calender.get(Calendar.YEAR)+" at "+calender.get(Calendar.HOUR_OF_DAY)+"h"+calender.get(Calendar.MINUTE);
		return date;
	}

	private ArrayList<CarOrder> getSortedCompletedOrder(GarageHolder user, OrderManager ordermanager)  {
		ArrayList<CarOrder> Orders = ordermanager.getCompletedOrders(user);
		ArrayList<CarOrder> result = new ArrayList<CarOrder>();
		while(!Orders.isEmpty()){
			CarOrder min = Orders.get(0);
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

	public void carMechanicCase(CarMechanic carMechanic){
		// 1. The system asks the user what work post he is currently residing at
		LinkedList<Workstation> workstations = company.getAllWorkstations();
		Workstation workstation = (Workstation) ui.askWithPossibilities("Which workstation are you currently residing at?", workstations.toArray().clone());
		// 2. The user selects the corresponding work post.
		workstation.addCarMechanic(carMechanic);
		while(true) {
			// 3. The system presents an overview of the pending assembly tasks for the
			// car at the current work post.
			if (workstation.getAllPendingTasks().isEmpty()) {
				ui.display("This workstation has no pending assembly tasks. Please try again later or go to another workstation.");
				break;
			}
			// 4. The user selects one of the assembly tasks.
			ArrayList<AssemblyTask> tasks = workstation.getAllPendingTasks();
			AssemblyTask task = (AssemblyTask) ui.askWithPossibilities("Which pending task do you want to work on?", tasks.toArray().clone());
			workstation.selectTask(task);
			// 5. The system shows the assembly task information, including the
			// sequence of actions to perform.
			ui.display(workstation.getActiveTaskInformation().toArray());
			// 6. The user performs the assembly tasks and indicates when the assembly
			// task is finished.
			//TODO wel vrij stom dat je hier enkel Yes kan zeggen, want No wordt gevolgd door dezelfde vraag.
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

	@Override
	public List<String> getPossibleOptionsOfType(OrderForm order ,String type) {
		List<String> result = new ArrayList<String>();

		CarModel model = getCarModel(order.getModel());
		for(Option i: model.getOptions()){
			if(i.getType().toString().equals(type)){
				Boolean incompatible = false;
				for(String k: order.getOptions()){
					Option j = getOption(k);
					incompatible=	incompatible || j.conflictsWith(i);
				}
				if(!incompatible) result.add(i.getDescription());
			}
		}
		return result;
	}

	@Override
	public boolean canPlaceType(OrderForm order ,String Type) {
		Boolean temp = false;
		for(OptionType validType: OptionType.values()){
			if(validType.equals(OptionType.valueOf(Type))) temp = true;
		}
		if(!temp) return false;
		for(String input: order.getOptions()){
			if(getOption(input).getType().equals(OptionType.valueOf(Type))) return false;
		}
		return true;
	}

	public List<String> getOptionTypes() {
		ArrayList<String> result = new ArrayList<String>();
		for(OptionType i:OptionType.values()) result.add(i.toString());
		return result;
	}

	/**
	 * Get a car model based on the name
	 * @param name the name
	 * @return a car model based with the name name
	 * 	       null if the name does not match a model 
	 */
	private CarModel getCarModel(String name){
		CarModelCatalog catalog = company.getCatalog();
		for(CarModel possible: catalog.getAllModels()){
			if(possible.getName().equals(name)) return possible;
		}
		return null;
	}
	/**
	 * Get a car option based on the description
	 * @param description the description
	 * @return a car option based with the description description
	 *         null if the description does not match an option
	 */
	private Option getOption(String description){
		CarModelCatalog catalog = company.getCatalog();
		for(Option possible: catalog.getAllOptions()){
			if(possible.getDescription().equals(description)) return possible;
		}
		return null;
	}
}

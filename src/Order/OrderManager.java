package Order;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import Assembly.ProductionSchedule;
import Car.CarModel;
import Car.CarOrder;
import Car.Option;
import User.GarageHolder;



public class OrderManager {

	private ProductionSchedule productionSchedule;
	private final HashMap<Integer,ArrayList<CarOrder>> carOrdersPerId;
	private int highestCarOrderID;

	/**
	 * Constructor for the OrderManager class.
	 * This OrderManager starts without any CarOrders.
	 * This constructor is also responsible for creating a ProductionSchedule.

	 * @param	currentTime 
	 * 			The Calendar indicating the current time and date used by the created ProductionSchedule.
	 */
	public OrderManager(GregorianCalendar currentTime) {
		this.carOrdersPerId = new HashMap<Integer,ArrayList<CarOrder>>();

		this.createProductionSchedule(currentTime);
	}
	

	
	/**
	 * Give a list of all the CarOrders placed by a given user.
	 * 
	 * @param 	user
	 * 			The User that wants to call this method.
	 * 			The User whose CarOrders are requested.
	 * @return	A copy of the list of all CarOrders made by the given user. An empty list if there are none.
	 * @throws	UserAccessException 
	 * 			If the user is not authorized to call this method.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<CarOrder> getOrders(GarageHolder user){

		ArrayList<CarOrder> ordersOfUser = this.getCarOrdersPerId().get(user.getId());
		if(ordersOfUser == null)
			return new ArrayList<CarOrder>();
		else
			return (ArrayList<CarOrder>) ordersOfUser.clone();
	}
	
	/**
	 * Create a CarOrder based on the given OrderForm and put it in a ProductionSchedule.
	 * 
	 * @param 	orderForm
	 * 			The OrderForm containing all the information necessary to place a CarOrder.
	 * @return	The CarOrder that was made with the given OrderForm.
	 * @throws 	UserAccessException
	 * 			If the given OrderForm is filled in by a user who is not authorized to place orders.
	 */
	public CarOrder placeOrder(GarageHolder user, CarModel model ,ArrayList<Option> options){

		int carOrderId = this.getUniqueCarOrderId();
		CarOrder newOrder = new CarOrder(carOrderId, user, model,options);
		this.addCarOrder(newOrder);
		this.getProductionSchedule().addOrder(newOrder);
		return newOrder;

	}


	/**
	 * Calculates an estimated completion date for the given CarOrder.
	 * 
	 * @param 	user
	 * 			The User that wants to call this method.
	 * @param 	order
	 * 			The CarOrder whose estimated completion date is requested.
	 * @return	A GregorianCalendar representing the estimated completion date of order.
	 * 			Or the actual delivery date if it was already completed.
	 * @throws 	UserAccessException
	 * 			If the user is not authorized to call this method.
	 */
	public GregorianCalendar completionEstimate(CarOrder order) {
		try{
		return order.getDeliveredTime();
		} catch(IllegalStateException e){
			return this.getProductionSchedule().completionEstimateCarOrder(order);
		}
		
			
	}

	/**
	 * Creates a ProductionSchedule which is initialized with the given CarOrders.
	 * 
	 * @param orderList
	 * 			The list of orders that has to be scheduled on the create ProductionSchedule.
	 * @param currentTime
	 * 			The date at which the created ProductionSchedule starts.
	 */
	private void createProductionSchedule(GregorianCalendar currentTime){
		ProductionSchedule newProductionSchedule = new ProductionSchedule(currentTime);
		this.setProductionSchedule(newProductionSchedule);
	}

	
	/**
	 * Add a new CarOrder to the OrderManager 
	 * 
	 * @param 	newOrder
	 * 			The CarOrder which will be added.
	 */
	private void addCarOrder(CarOrder newOrder) {
		if(!this.getCarOrdersPerId().containsKey(newOrder.getUserId()))
		{
			this.getCarOrdersPerId().put(newOrder.getUserId(), new ArrayList<CarOrder>());
		}
		this.getCarOrdersPerId().get(newOrder.getUserId()).add(newOrder);
	}
	
	/**
	 * Returns a CarOrderId which is higher than all other carOrderId of the CarOrders in this OrderManager
	 * 
	 * @return a CarOrderId
	 */
	private int getUniqueCarOrderId() {
		this.highestCarOrderID += 1;
		return this.highestCarOrderID;
	}
	
	public ProductionSchedule getProductionSchedule() {
		return productionSchedule;
	}

	private void setProductionSchedule(ProductionSchedule productionSchedule) {
		this.productionSchedule = productionSchedule;
	}

	private HashMap<Integer, ArrayList<CarOrder>> getCarOrdersPerId() {
		return carOrdersPerId;
	}

	/**
	 * Give a list of all the still pending CarOrders placed by a given user.
	 * @param user
	 * 			The User that wants to call this method.
	 * 			The User whose CarOrders are requested.
	 * @return List of the pending CarOrders placed by the given user.
	 * @throws UserAccessException
	 * 			If the user is not authorized to call this method.
	 */
	public ArrayList<CarOrder> getPendingOrders(GarageHolder user) {
		return GetOrdersWithStatus(user, false);
	}

	/**
	 * Give a list of all the still completed CarOrders placed by a given user.
	 * @param user
	 * 			The User that wants to call this method.
	 * 			The User whose CarOrders are requested.
	 * @return List of the completed CarOrders placed by the given user.
	 * @throws UserAccessException
	 * 			If the user is not authorized to call this method.
	 */
	public ArrayList<CarOrder> getCompletedOrders(GarageHolder user){
		return GetOrdersWithStatus(user,true);
	}
	
	/**
	 * Give a list of  CarOrders placed by a given user, with the boolean indicating if they have to be completed or not.
	 * @param user
	 * 			The User that wants to call this method.
	 * 			The User whose CarOrders are requested.
	 * @param b
	 * 			The boolean indicating completion
	 * @return List of CarOrders placed by the given user.
	 * @throws UserAccessException
	 * 			If the user is not authorized to call this method.
	 */
	private ArrayList<CarOrder> GetOrdersWithStatus(GarageHolder user, boolean b){
		ArrayList<CarOrder> result = new ArrayList<CarOrder>();
		for(CarOrder i : this.getOrders(user)){
			if(i.isCompleted().equals(b)) result.add(i);
		}
		return result;
	}
}

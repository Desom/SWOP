package domain.order;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import domain.assembly.ProductionSchedule;
import domain.configuration.CarModelCatalog;
import domain.configuration.Configuration;
import domain.configuration.OptionType;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.policies.SingleTaskOrderNumbersOfTasksPolicy;
import domain.policies.SingleTaskOrderTaskTypePolicy;
import domain.user.CustomShopManager;
import domain.user.GarageHolder;



public class OrderManager {

	private ProductionSchedule productionSchedule;
	private final HashMap<Integer,ArrayList<Order>> ordersPerId;
	private int highestCarOrderID;
	private Policy singleTaskPolicy;
	private Policy carOrderPolicy;
	
	/**
	 * Constructor for the OrderManager class.
	 * This constructor is also responsible for creating objects for all the placed carOrders.
	 * This constructor is also responsible for creating a ProductionSchedule and feeding it the unfinished carOrders.
	 * 
	 * @param	dataFilePath
	 * 			The data file containing all the previously placed CarOrders. 
	 * @param 	catalog
	 * 			The CarModelCatalog necessary for finding the Options and CarModel objects of all CarOrders
	 * @param	currentTime 
	 * 			The Calendar indicating the current time and date used by the created ProductionSchedule.
	 * @throws InvalidConfigurationException 
	 */
	public OrderManager(String dataFilePath, CarModelCatalog catalog, GregorianCalendar currentTime) throws InvalidConfigurationException {
		this.createPolicies();
		CarOrderCreator carOrderCreator = new CarOrderCreator(dataFilePath, catalog, this.carOrderPolicy );
		ArrayList<CarOrder> allCarOrders = carOrderCreator.createCarOrderList();
		//TODO create single task order list?
		this.ordersPerId = new HashMap<Integer,ArrayList<Order>>();
		for(CarOrder order : allCarOrders) {
			this.addOrder(order);
		}

		ArrayList<CarOrder> allUnfinishedCarOrders = new ArrayList<CarOrder>();
		for(CarOrder order : allCarOrders) {
			if(order.getCarOrderID() > this.highestCarOrderID){
				this.highestCarOrderID = order.getCarOrderID();
			}
			if(!order.isCompleted()){
				allUnfinishedCarOrders.add(order);
			}
		}
		this.createProductionSchedule(allUnfinishedCarOrders, currentTime);
		
	}
	
	/**
	 * Constructor for the OrderManager class.
	 * This OrderManager starts without any CarOrders.
	 * This constructor is also responsible for creating a ProductionSchedule.

	 * @param	currentTime 
	 * 			The Calendar indicating the current time and date used by the created ProductionSchedule.
	 */
	public OrderManager(GregorianCalendar currentTime) {
		this.ordersPerId = new HashMap<Integer,ArrayList<Order>>();
		this.highestCarOrderID = 0;
		this.createProductionSchedule(new ArrayList<CarOrder>(), currentTime);
		this.createPolicies();
	}
	

	
	/**
	 * Give a list of all the orders placed by a given user.
	 * 
	 * @param 	user
	 * 			The User that wants to call this method.
	 * 			The User whose CarOrders are requested.
	 * @return	A copy of the list of all orders made by the given user. An empty list if there are none.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Order> getOrders(GarageHolder user){

		ArrayList<Order> ordersOfUser = this.getAllOrdersPerId().get(user.getId());
		if(ordersOfUser == null)
			return new ArrayList<Order>();
		else
			return (ArrayList<Order>) ordersOfUser.clone();
	}
	
	/**
	 * Create an order based on the given OrderForm and put it in a ProductionSchedule.
	 * 
	 * @param 	orderForm
	 * 			The OrderForm containing all the information necessary to place a CarOrder.
	 * @return	The order that was made with the given OrderForm.
	 */
	public CarOrder placeCarOrder(GarageHolder user, Configuration configuration){
		int carOrderId = this.getUniqueCarOrderId();
		CarOrder newOrder = new CarOrder(carOrderId, user, configuration);
		this.addOrder(newOrder);
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
	private void createProductionSchedule(List<CarOrder> carOrders, GregorianCalendar currentTime){
		ProductionSchedule newProductionSchedule = new ProductionSchedule(carOrders, currentTime);
		this.setProductionSchedule(newProductionSchedule);
	}

	
	/**
	 * Add a new order to the OrderManager 
	 * 
	 * @param 	newOrder
	 * 			The order which will be added.
	 */
	private void addOrder(Order newOrder) {
		if(!this.getAllOrdersPerId().containsKey(newOrder.getUserId()))
		{
			this.getAllOrdersPerId().put(newOrder.getUserId(), new ArrayList<Order>());
		}
		this.getAllOrdersPerId().get(newOrder.getUserId()).add(newOrder);
	}
	
	/**
	 * Returns a orderId which is higher than all other orderId of the orders in this OrderManager.
	 * 
	 * @return a orderId
	 */
	private int getUniqueCarOrderId() {
		this.highestCarOrderID += 1;
		return this.highestCarOrderID;
	}

	 //TODO docs
	public ProductionSchedule getProductionSchedule() {
		return productionSchedule;
	}

	 //TODO docs
	private void setProductionSchedule(ProductionSchedule productionSchedule) {
		this.productionSchedule = productionSchedule;
	}

	 //TODO docs
	private HashMap<Integer, ArrayList<Order>> getAllOrdersPerId() {
		return ordersPerId;
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
	public ArrayList<Order> getPendingOrders(GarageHolder user) {
		return getOrdersWithStatus(user, false);
	}

	/**
	 * Give a list of all the still completed CarOrders placed by a given user.
	 * @param user
	 * 			The User that wants to call this method.
	 * 			The User whose CarOrders are requested.
	 * @return List of the completed CarOrders placed by the given user.
	 */
	public ArrayList<Order> getCompletedOrders(GarageHolder user){
		return getOrdersWithStatus(user,true);
	}
	
	/**
	 * Give a list of orders placed by a given user, with the boolean indicating if they have to be completed or not.
	 * @param user
	 * 			The User that wants to call this method.
	 * 			The User whose orders are requested.
	 * @param bool
	 * 			The boolean indicating completion
	 * @return List of orders placed by the given user.
	 */
	private ArrayList<Order> getOrdersWithStatus(GarageHolder user, boolean bool){
		ArrayList<Order> result = new ArrayList<Order>();
		for(Order i : this.getOrders(user)){
			if(i.isCompleted().equals(bool)) result.add(i);
		}
		return result;
	}

	 //TODO docs
	private void createPolicies(){
		createSingleTaskPolicy();
		createCarOrderPolicy();
	}

	 //TODO docs
	private void createCarOrderPolicy() {
		ArrayList<OptionType> List = new ArrayList<OptionType>();
		for(OptionType i: OptionType.values()){
			if(i != OptionType.Airco || i != OptionType.Spoiler ){
				List.add(i);
			}
		}
		Policy pol1 = new CompletionPolicy(null,List);
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);
		this.carOrderPolicy= pol4;
		
	}

	 //TODO docs
	private void createSingleTaskPolicy() {
		Policy pol1 = new SingleTaskOrderTaskTypePolicy(null);
		Policy pol2 = new SingleTaskOrderNumbersOfTasksPolicy(pol1);
		this.singleTaskPolicy = pol2;
	}

	 //TODO docs
	public Policy getCarOrderPolicies(){
		return this.carOrderPolicy;
		 
	 }
	 
	 //TODO docs
	 public Policy getSingleTaskOrderPolicies(){
			return this.singleTaskPolicy;
			 
		 }

	 /**
	  * Returns all the orders that are not completed yet.
	  * 
	  * @return ArrayList of incomplete orders.
	  */
	public ArrayList<Order> getAllUnfinishedOrders() {
		ArrayList<Order> unfinished = new ArrayList<Order>();
		
		for(ArrayList<Order> carOrders : this.ordersPerId.values()){
			for(Order order : carOrders){
				if(!order.isCompleted()){
					unfinished.add(order);
				}
			}
		}
		return unfinished;
	}

	public SingleTaskOrder placeSingleTaskOrder(CustomShopManager customShopManager, Configuration configuration,
			GregorianCalendar deadline) {
		new SingleTaskOrder(highestCarOrderID, customShopManager, configuration,deadline);
		int carOrderId = this.getUniqueCarOrderId();
		SingleTaskOrder newOrder = new SingleTaskOrder(carOrderId, customShopManager, configuration,deadline);
		this.addOrder(newOrder);
		this.getProductionSchedule().addOrder(newOrder);
		return newOrder;

	}
}

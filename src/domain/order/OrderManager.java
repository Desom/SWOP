package domain.order;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import domain.assembly.Scheduler;
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
import domain.user.User;

public class OrderManager {

	private final Scheduler scheduler;
	private final HashMap<User,ArrayList<Order>> ordersPerUser;
	private int highestCarOrderID;
	private Policy singleTaskPolicy;
	private Policy carOrderPolicy;
	
	/**
	 * Constructor for the OrderManager class.
	 * This constructor is also responsible for creating objects for all the placed carOrders.
	 * This constructor is also responsible for creating a ProductionSchedule and feeding it the unfinished carOrders.
	 * 
	 * @param scheduler
	 * 		The scheduler for the orders.
	 * @param dataFilePath
	 * 		The path to the data file containing all the previously placed CarOrders.
	 * @param catalog
	 * 		The car model catalog necessary for finding the options and car models of all car orders
	 * @param currentTime 
	 * 		The Calendar indicating the current time and date used by the created ProductionSchedule.
	 * @throws InvalidConfigurationException 
	 * 		If the configurations made in car order creator are invalid.
	 */
	public OrderManager(Scheduler scheduler, String dataFilePath, CarModelCatalog catalog, GregorianCalendar currentTime) throws InvalidConfigurationException {
		this.scheduler = scheduler;
		this.createPolicies();
		CarOrderCreator carOrderCreator = new CarOrderCreator(dataFilePath, catalog, this.carOrderPolicy);
		ArrayList<Order> allCarOrders = carOrderCreator.createCarOrderList();
		
		this.ordersPerUser = new HashMap<User,ArrayList<Order>>();
		for(Order order : allCarOrders) {
			this.addOrder(order);
		}

		ArrayList<Order> allUnfinishedCarOrders = new ArrayList<Order>();
		for(Order order : allCarOrders) {
			if(order.getCarOrderID() > this.highestCarOrderID){
				this.highestCarOrderID = order.getCarOrderID();
			}
			if(!order.isCompleted()){
				allUnfinishedCarOrders.add(order);
			}
		}
		scheduler.setOrderManager(this);
	}
	
	/**
	 * Constructor for the OrderManager class.
	 * This OrderManager starts without any CarOrders.
	 * This constructor is also responsible for creating a ProductionSchedule.
	 * 
	 * @param scheduler
	 * 		The scheduler for the orders.
	 * @param currentTime 
	 * 		The Calendar indicating the current time and date used by the created ProductionSchedule.
	 */
	public OrderManager(Scheduler scheduler, GregorianCalendar currentTime) {
		this.scheduler = scheduler;
		this.ordersPerUser = new HashMap<User,ArrayList<Order>>();
		this.highestCarOrderID = 0;
		this.createPolicies();
		scheduler.setOrderManager(this);
	}
	
	/**
	 * Returns a list of all the orders placed by a given garage holder.
	 * 
	 * @param garageHolder
	 * 		The GarageHolder whose CarOrders are requested.
	 * @return A copy of the list of all orders made by the given GarageHolder. An empty list if there are none.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Order> getOrders(GarageHolder garageHolder){
		ArrayList<Order> ordersOfUser = this.getAllOrdersFromUser().get(garageHolder.getId());
		if(ordersOfUser == null)
			return new ArrayList<Order>();
		else
			return (ArrayList<Order>) ordersOfUser.clone();
	}
	
	/**
	 * Creates and places an order of the given garage holder based on the given configuration.
	 * 
	 * @param garageHolder
	 * 		The garage holder who is placing the order.
	 * @param configuration
	 * 		The configuration of the ordered car.
	 * @return The order that was made with the given configuration.
	 */
	public CarOrder placeCarOrder(GarageHolder garageHolder, Configuration configuration){
		if(configuration.getPolicyChain() != this.getCarOrderPolicies()){
			throw new IllegalArgumentException("The given Configuration doesn't have the right policy chain for a CarOrder.");
		}
		if(!configuration.isCompleted()){
			throw new IllegalArgumentException("The given Configuration is not yet completed.");
		}
		int carOrderId = this.getUniqueCarOrderId();
		CarOrder newOrder = new CarOrder(carOrderId, garageHolder, configuration, this.getScheduler().getCurrentTime());
		this.addOrder(newOrder);
		return newOrder;
	}

	/**
	 * Calculates an estimated completion time for the given order.
	 * 
	 * @param 	order
	 * 			The order whose estimated time is calculated.
	 * @return	A GregorianCalendar representing the estimated completion date of the order.
	 * 			Or the actual delivery date if it was already completed.
	 */
	public GregorianCalendar completionEstimate(Order order) {
		try{
		return order.getDeliveredTime();
		} catch(IllegalStateException e){
			return this.getScheduler().completionEstimate(order);
		}
	}
	
	/**
	 * Adds a new order to the OrderManager. 
	 * 
	 * @param newOrder
	 * 		The order which will be added.
	 */
	private void addOrder(Order newOrder){
		if(!this.getAllOrdersFromUser().containsKey(newOrder.getUser()))
		{
			this.getAllOrdersFromUser().put(newOrder.getUser(), new ArrayList<Order>());
		}
		this.getAllOrdersFromUser().get(newOrder.getUser()).add(newOrder);
		this.getScheduler().updateSchedule();
	}
	
	/**
	 * Returns an orderId which is higher than all other orderId's of the orders in this OrderManager.
	 * 
	 * @return an orderId which is higher than all other orderId's of the orders in this OrderManager
	 */
	private int getUniqueCarOrderId() {
		this.highestCarOrderID += 1;
		return this.highestCarOrderID;
	}

	/**
	 * Returns the scheduler of this order manager.
	 * 
	 * @return The scheduler of this order manager.
	 */
	public Scheduler getScheduler() {
		return this.scheduler;
	}

	/**
	 * Returns a hash map with all users as keys and orders as values.
	 */
	private HashMap<User, ArrayList<Order>> getAllOrdersFromUser() {
		return ordersPerUser;
	}

	/**
	 * Returns a list of all still pending car orders placed by a given user.
	 * 
	 * @param garageHolder
	 * 		The User that wants to call this method.
	 * 		The User whose CarOrders are requested.
	 * @return List of the pending CarOrders placed by the given user.
	 * @throws UserAccessException
	 * 		If the user is not authorized to call this method.
	 */
	public ArrayList<Order> getPendingOrders(GarageHolder garageHolder) {
		return getOrdersWithStatus(garageHolder, false);
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
	 * 
	 * @param user
	 * 		The User whose orders are requested.
	 * @param completed
	 * 		The boolean indicating if the orders have to be completed.
	 * @return The list of orders placed by the given user, completed or not completed (depending on the boolean).
	 */
	private ArrayList<Order> getOrdersWithStatus(GarageHolder user, boolean completed){
		ArrayList<Order> orders = new ArrayList<Order>();
		for(Order order : this.getOrders(user)){
			if(order.isCompleted().equals(completed)) orders.add(order);
		}
		return orders;
	}

	/**
	 * Creates all policy chains.
	 */
	private void createPolicies(){
		createSingleTaskPolicy();
		createCarOrderPolicy();
	}

	/**
	 * Creates the car order policy chain.
	 */
	private void createCarOrderPolicy() {
		Policy pol1 = new CompletionPolicy(null,OptionType.getAllMandatoryTypes());
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);
		this.carOrderPolicy = pol4;
		
	}

	/**
	 * Creates the single task order policy chain.
	 */
	private void createSingleTaskPolicy() {
		Policy pol1 = new SingleTaskOrderTaskTypePolicy(null);
		Policy pol2 = new SingleTaskOrderNumbersOfTasksPolicy(pol1);
		this.singleTaskPolicy = pol2;
	}

	 /**
	  * Returns the car order policy chain.
	  * 
	  * @return the car order policy chain.
	  */
	public Policy getCarOrderPolicies(){
		return this.carOrderPolicy;
	 }
	 
	 /**
	  * Returns the single task order policy chain.
	  * 
	  * @return the single task order policy chain.
	  */
	 public Policy getSingleTaskOrderPolicies(){
			return this.singleTaskPolicy;
		 }

	/**
	 * Places a single task order associated with the given custom shop manager, configuration and deadline.
	 * Also returns this single task order.
	 * 
	 * @param customShopManager
	 * 		The custom shop manager who wants to place the order.
	 * @param configuration
	 * 		The configuration of the order.
	 * @param deadline
	 * 		The deadline of the order.
	 * @return The new single task order that has been placed.
	 */
	public SingleTaskOrder placeSingleTaskOrder(CustomShopManager customShopManager, Configuration configuration, GregorianCalendar deadline) {
		if (this.scheduler.getCurrentTime().after(deadline)) {
			throw new IllegalArgumentException("The deadline is in the past");
		}
		if(configuration.getPolicyChain() != this.getSingleTaskOrderPolicies()){
			throw new IllegalArgumentException("The given Configuration doesn't have the right policy chain for a SingleTaskOrder.");
		}
		if(!configuration.isCompleted()){
			throw new IllegalArgumentException("The given Configuration is not yet completed.");
		}
		int carOrderId = this.getUniqueCarOrderId();
		SingleTaskOrder newOrder = new SingleTaskOrder(carOrderId, customShopManager, configuration, this.getScheduler().getCurrentTime(), deadline);
		this.addOrder(newOrder);
		return newOrder;
	}
	
	/**
	  * Returns all the orders that are not completed yet.
	  * 
	  * @return The list of all incomplete orders.
	  */
	public ArrayList<Order> getAllUnfinishedOrders() {
		ArrayList<Order> unfinished = new ArrayList<Order>();
		
		for(ArrayList<Order> carOrders : this.ordersPerUser.values()){
			for(Order order : carOrders){
				if(!order.isCompleted()){
					unfinished.add(order);
				}
			}
		}
		return unfinished;
	}
	
	/**
	 * Returns a list of all completed orders for all users.
	 * 
	 * @return An ArrayList of all completed orders.
	 */
	public ArrayList<Order> getAllCompletedOrders(){
		ArrayList<Order> completed = new ArrayList<Order>();
		for(ArrayList<Order> olist : this.ordersPerUser.values()){
			for(Order o : olist){
				if(o.isCompleted())
				completed.add(o);			
			}
		}
		return completed;
	}
}

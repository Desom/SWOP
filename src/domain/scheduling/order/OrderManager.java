package domain.scheduling.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import domain.configuration.VehicleCatalog;
import domain.configuration.Configuration;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.policies.SingleTaskOrderNumbersOfTasksPolicy;
import domain.policies.SingleTaskOrderTaskTypePolicy;
import domain.scheduling.CannotMeetDeadlineException;
import domain.scheduling.schedulers.OrderHandler;
import domain.scheduling.schedulers.Scheduler;
import domain.user.CustomShopManager;
import domain.user.GarageHolder;
import domain.user.User;

public class OrderManager implements OrderHandler{

	private final Scheduler scheduler;
	private final HashMap<User,ArrayList<Order>> ordersPerUser;
	private int highestOrderID;
	private Policy singleTaskPolicy;
	private Policy vehicleOrderPolicy;
	
	/**
	 * Constructor for the OrderManager class.
	 * This constructor is also responsible for creating objects for all the placed Orders.
	 * 
	 * @param scheduler
	 * 		The scheduler for the orders.
	 * @param dataFilePath
	 * 		The path to the data file containing all the previously placed Orders.
	 * @param catalog
	 * 		The model catalog necessary for finding the options and models of all orders
	 * @throws InvalidConfigurationException 
	 * 		If the configurations made in order creator are invalid.
	 * @throws IOException
	 * 		If a model can't be read in.
	 */
	public OrderManager(Scheduler scheduler, String dataFilePath, VehicleCatalog catalog) throws IOException {
		this.scheduler = scheduler;
		scheduler.setOrderHandler(this);
		this.createPolicies();
		ArrayList<Policy> policies = new ArrayList<Policy>();
		policies.add(vehicleOrderPolicy);
		policies.add(singleTaskPolicy);
		OrderCreatorInterface orderCreator = new OrderCreator(dataFilePath, catalog, policies);
		ArrayList<Order> allOrders = orderCreator.createOrderList();

		scheduler.setOrderHandler(this);
		
		this.ordersPerUser = new HashMap<User,ArrayList<Order>>();
		for(Order order : allOrders) {
			if(!this.getAllOrdersFromUser().containsKey(order.getUser()))
			{
				this.getAllOrdersFromUser().put(order.getUser(), new ArrayList<Order>());
			}
			this.getAllOrdersFromUser().get(order.getUser()).add(order);
		}

		ArrayList<Order> allUnfinishedOrders = new ArrayList<Order>();
		for(Order order : allOrders) {
			if(order.getOrderID() > this.highestOrderID){
				this.highestOrderID = order.getOrderID();
			}
			if(!order.isCompleted()){
				allUnfinishedOrders.add(order);
			}
		}
		
		this.scheduler.updateSchedule();
	}
	
	/**
	 * Constructor for the OrderManager class.
	 * This OrderManager starts without any Orders.
	 * 
	 * @param scheduler
	 * 		The scheduler for the orders.
	 */
	public OrderManager(Scheduler scheduler) {
		this.scheduler = scheduler;
		this.ordersPerUser = new HashMap<User,ArrayList<Order>>();
		this.highestOrderID = 0;
		this.createPolicies();
		scheduler.setOrderHandler(this);
	}
	
	/**
	 * Returns a list of all the orders placed by a given user.
	 * 
	 * @param user
	 * 		The user whose Orders are requested.
	 * @return A copy of the list of all orders made by the given user. An empty list if there are none.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Order> getOrders(User user){
		ArrayList<Order> ordersOfUser = this.getAllOrdersFromUser().get(user);
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
	 * 		The configuration of the ordered vehicle.
	 * @return The order that was made with the given configuration.
	 */
	public VehicleOrder placeVehicleOrder(GarageHolder garageHolder, Configuration configuration){
		if(configuration.getPolicyChain() != this.getVehicleOrderPolicies()){
			throw new IllegalArgumentException("The given Configuration doesn't have the right policy chain for a VehicleOrder.");
		}
		if(!configuration.isCompleted()){
			throw new IllegalArgumentException("The given Configuration is not yet completed.");
		}
		int orderId = this.getUniqueOrderId();
		VehicleOrder newOrder = new VehicleOrder(orderId, garageHolder, configuration, this.getScheduler().getCurrentTime());
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
	private int getUniqueOrderId() {
		this.highestOrderID += 1;
		return this.highestOrderID;
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
	 * Returns a list of all still pending orders placed by a given user.
	 * 
	 * @param user
	 * 		The User that wants to call this method.
	 * 		The User whose Orders are requested.
	 * @return List of the pending Orders placed by the given user.
	 * @throws UserAccessException
	 * 		If the user is not authorized to call this method.
	 */
	public ArrayList<Order> getPendingOrders(User user) {
		return getOrdersWithStatus(user, false);
	}

	/**
	 * Give a list of all the still completed Orders placed by a given user.
	 * @param user
	 * 			The User that wants to call this method.
	 * 			The User whose Orders are requested.
	 * @return List of the completed Orders placed by the given user.
	 */
	public ArrayList<Order> getCompletedOrders(User user){
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
	private ArrayList<Order> getOrdersWithStatus(User user, boolean completed){
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
		createVehicleOrderPolicy();
	}

	/**
	 * Creates the vehicle order policy chain.
	 */
	private void createVehicleOrderPolicy() {
		Policy pol1 = new CompletionPolicy(null,VehicleCatalog.taskTypeCreator.getAllMandatoryTypes());
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);
		this.vehicleOrderPolicy = pol4;
		
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
	  * Returns the vehicle order policy chain.
	  * 
	  * @return the vehicle order policy chain.
	  */
	public Policy getVehicleOrderPolicies(){
		return this.vehicleOrderPolicy;
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
	 * @throws CannotMeetDeadlineException 
	 * 		If the deadline for this SingleTaskOrder cannot be met.
	 * @throws IllegalArgumentException
	 * 		If the given deadline has already passed.
	 * 		If the given Configuration doesn't have the right policy chain.
	 * 		If the given Configuration isn't completed yet.
	 */
	public SingleTaskOrder placeSingleTaskOrder(CustomShopManager customShopManager, Configuration configuration, GregorianCalendar deadline) throws CannotMeetDeadlineException {
		if (this.scheduler.getCurrentTime().after(deadline)) {
			throw new IllegalArgumentException("The deadline is in the past");
		}
		if(configuration.getPolicyChain() != this.getSingleTaskOrderPolicies()){
			throw new IllegalArgumentException("The given Configuration doesn't have the right policy chain for a SingleTaskOrder.");
		}
		if(!configuration.isCompleted()){
			throw new IllegalArgumentException("The given Configuration is not yet completed.");
		}
		int orderId = this.getUniqueOrderId();
		SingleTaskOrder newOrder = new SingleTaskOrder(orderId, customShopManager, configuration, this.getScheduler().getCurrentTime(), deadline);
		if(!this.getScheduler().canFinishOrderBeforeDeadline(newOrder)){
			throw new CannotMeetDeadlineException();
		}
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
		
		for(ArrayList<Order> orders : this.ordersPerUser.values()){
			for(Order order : orders){
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


	/**
	 * Returns the orders it wants the given scheduler to schedule.
	 * 
	 * @param scheduler
	 * 		The Scheduler that will schedule the returned Orders.
	 * @return The Orders which have to be scheduled by the given scheduler.
	 */
	//TODO wat als scheduler niet hoort bij deze orderhandler? null,exception,lege lijst??? Zie OrderManager,FactoryScheduler
	@Override
	public ArrayList<Order> getOrdersFor(Scheduler scheduler) {
		if(!scheduler.equals(this.getScheduler())){
			//TODO wat moet er gebeuren?
		}
		
		ArrayList<Order> orderList = new ArrayList<Order>();
		
		//TODO is deze controle goed? of moet gewoon alles aan de scheduler worden gegeven?
		for(Order order: this.getAllUnfinishedOrders()){
			if(this.getScheduler().canScheduleOrder(order)){
				orderList.add(order);
			}
		}
		
		return orderList;
	}

	@Override
	public boolean hasScheduler(Scheduler scheduler) {
		return scheduler != null && scheduler.equals(this.scheduler);
	}
}

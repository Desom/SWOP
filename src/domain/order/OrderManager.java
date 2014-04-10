package domain.order;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import domain.assembly.ProductionSchedule;
import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.policies.SingleTaskOrderNumbersOfTasksPolicy;
import domain.policies.SingleTaskOrderTaskTypePolicy;
import domain.user.GarageHolder;



public class OrderManager {

	private ProductionSchedule productionSchedule;
	private final HashMap<Integer,ArrayList<CarOrder>> carOrdersPerId;
	private int highestCarOrderID;
	private Policy singleTaskPolicy;
	private Policy CarOrderPolicy;
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
		CarOrderCreator carOrderCreator = new CarOrderCreator(dataFilePath, catalog, this.CarOrderPolicy );
		ArrayList<CarOrder> allCarOrders = carOrderCreator.createCarOrderList();
		this.carOrdersPerId = new HashMap<Integer,ArrayList<CarOrder>>();
		for(CarOrder order : allCarOrders) {
			this.addCarOrder(order);
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
		this.carOrdersPerId = new HashMap<Integer,ArrayList<CarOrder>>();
		this.highestCarOrderID = 0;
		this.createProductionSchedule(new ArrayList<CarOrder>(), currentTime);
		this.createPolicies();
	}
	

	
	/**
	 * Give a list of all the CarOrders placed by a given user.
	 * 
	 * @param 	user
	 * 			The User that wants to call this method.
	 * 			The User whose CarOrders are requested.
	 * @return	A copy of the list of all CarOrders made by the given user. An empty list if there are none.
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
	 */
	public CarOrder placeOrder(GarageHolder user, Configuration configuration){

		int carOrderId = this.getUniqueCarOrderId();
		CarOrder newOrder = new CarOrder(carOrderId, user, configuration);
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
	 */
	private ArrayList<CarOrder> GetOrdersWithStatus(GarageHolder user, boolean b){
		ArrayList<CarOrder> result = new ArrayList<CarOrder>();
		for(CarOrder i : this.getOrders(user)){
			if(i.isCompleted().equals(b)) result.add(i);
		}
		return result;
	}
	
	private void createPolicies(){
		createSingleTaskPolicy();
		createCarOrderPolicy();
	}

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
		this.CarOrderPolicy= pol4;
		
	}

	private void createSingleTaskPolicy() {
		Policy pol1 = new SingleTaskOrderTaskTypePolicy(null);
		Policy pol2 = new SingleTaskOrderNumbersOfTasksPolicy(pol1);
		this.singleTaskPolicy = pol2;
	}
	 public Configuration giveCarOrderConfiguration(CarModel model){
		return new Configuration(model,CarOrderPolicy);
		 
	 }
	 public Configuration SingleTaskOrderConfiguration(){
			return new Configuration(null,singleTaskPolicy);
			 
		 }
}

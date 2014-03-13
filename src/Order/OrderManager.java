package Order;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import Assembly.ProductionSchedule;
import Car.CarModel;
import Car.CarOrder;
import Car.Option;
import User.User;
import User.UserAccessException;



public class OrderManager {

	private ProductionSchedule productionSchedule;
	private final HashMap<Integer,ArrayList<CarOrder>> carOrdersPerId;
	private int highestCarOrderID;

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
	 */
	public OrderManager(String dataFilePath, CarModelCatalog catalog, GregorianCalendar currentTime) {
		ArrayList<CarOrder> allCarOrders = this.createOrderList(dataFilePath,catalog);
		this.carOrdersPerId = new HashMap<Integer,ArrayList<CarOrder>>();
		for(CarOrder order : allCarOrders) {
			this.addCarOrder(order);
		}

		ArrayList<CarOrder> allUnfinishedCarOrders = new ArrayList<CarOrder>();
		for(CarOrder order : allCarOrders) {
			if(!order.isCompleted()){
				allUnfinishedCarOrders.add(order);
			}
		}
		this.createProductionSchedule(allUnfinishedCarOrders, currentTime);
	}
	
	/**
	 * Constructor for the OrderManager class.
	 * This constructor is also responsible for creating objects for all the placed carOrders.
	 * This constructor is also responsible for creating a ProductionSchedule and feeding it the unfinished carOrders.
	 * The default "carOrderData.txt" file will be used to create all the previously placed CarOrders
	 * 
	 * @param	dataFilePath
	 * @param 	catalog
	 * 			The CarModelCatalog necessary for finding the Options and CarModel Objects of all CarOrders
	 * @param	currentTime 
	 * 			The Calendar indicating the current time and date used by the created ProductionSchedule.
	 */
	public OrderManager(CarModelCatalog catalog, GregorianCalendar currentTime){
		this("carOrderData.txt", catalog, currentTime);
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
	public ArrayList<CarOrder> getOrders(User user) throws UserAccessException{
		this.checkUser(user, "getOrders");

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
	public CarOrder placeOrder(OrderForm orderForm) throws UserAccessException{
		User user = orderForm.getUser();
		this.checkUser(user, "placeOrder");

		int carOrderId = this.getUniqueCarOrderId();
		CarOrder newOrder = new CarOrder(carOrderId, user,orderForm.getModel(),orderForm.getOptions());
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
	public GregorianCalendar completionEstimate(User user, CarOrder order) throws UserAccessException{
		this.checkUser(user, "completionEstimate");
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
	private void createProductionSchedule(ArrayList<CarOrder> orderList, GregorianCalendar currentTime){
		ProductionSchedule newProductionSchedule = new ProductionSchedule(orderList, currentTime);
		this.setProductionSchedule(newProductionSchedule);
	}
	
	/**
	 * Creates all the placed CarOrders.
	 * 
	 * @param	dataFile
	 * 			The file from which the CarOrders will be read.
	 * @param 	catalog
	 * 			The CarModelCatalog used to convert Strings to Option and CarModel objects.
	 * @return	A list of all the placed CarOrders.
	 */
	private ArrayList<CarOrder> createOrderList(String dataFile, CarModelCatalog catalog){
		ArrayList<CarOrder> allCarOrders = new ArrayList<CarOrder>();
		ArrayList<String> allCarOrderInfo = new ArrayList<String>();
		try {
			FileInputStream fStream = new FileInputStream(dataFile);
			DataInputStream dinStream = new DataInputStream(fStream);
			InputStreamReader insReader = new InputStreamReader(dinStream);
			BufferedReader bReader = new BufferedReader(insReader);
			bReader.readLine();
			String otherLine = bReader.readLine();
			while(!otherLine.startsWith("End")){
				allCarOrderInfo.add(otherLine);
				otherLine = bReader.readLine();
			}
			bReader.close();
		} catch (IOException e) {
			return null;
		}
		int highestID = 0;
		for(String orderStr: allCarOrderInfo){
			String[] orderPieces = orderStr.split(",,,,");
			// String omvormen naar objecten
		// 0 : carOrderId
			int carOrderId = Integer.parseInt(orderPieces[0]);
			if(carOrderId > highestID)
				highestID = carOrderId;
		// 1 : garageHolderId
			int garageHolderId = Integer.parseInt(orderPieces[1]);
		// 2 : isDelivered -> Boolean
			boolean isDelivered = false;
			if(orderPieces[2].equals("1")){
				isDelivered = true;
			}
			
		// 3 : orderedTime -> GregorianCalendar
			GregorianCalendar orderedCalendar = this.createCalendarFor(orderPieces[3]);
		// 4 : deliveryTime -> GregorianCalendar
			GregorianCalendar deliveredCalendar = null;
			if(isDelivered){
				deliveredCalendar = this.createCalendarFor(orderPieces[4]);
			}
		// 5 : modelId -> CarModel (we hebben hiervoor de Catalog nodig, hoe komen we daar aan?)
			CarModel model = catalog.getCarModel(orderPieces[5]);
		// 6 : options -> ArrayList<Option> (ook Catalog nodig)
			ArrayList<Option> optionsList = new ArrayList<Option>();
			String[] optionStr = orderPieces[6].split(";-;");
			for(String optionDescr: optionStr){
				optionsList.add(catalog.getOption(optionDescr));
			}
			allCarOrders.add(new CarOrder(carOrderId, garageHolderId, orderedCalendar, deliveredCalendar, model, optionsList));
		}

		this.highestCarOrderID = highestID;
		
		return allCarOrders;
	}

	/**
	 * Create a GregorianCalendar based on the given time and date.
	 * 
	 * @param	info
	 * 			The String that has to be converted to a GregorianCalendar object; format=DD-MM-YYYY*HH:MM:SS
	 * @return	A GregorianCalendar
	 */
	private GregorianCalendar createCalendarFor(String info) {
		String[] dateTime = info.split("==");
		String[] dateStr = dateTime[0].split("-");
		String[] timeStr = dateTime[1].split("-");
		int[] dateInt = new int[3];
		int[] timeInt = new int[3];
		for(int i = 0; i < 3;i++){
			dateInt[i] = Integer.parseInt(dateStr[i]);
			timeInt[i] = Integer.parseInt(timeStr[i]);
		}
		return new GregorianCalendar(dateInt[2],dateInt[1],dateInt[0],timeInt[0],timeInt[1],timeInt[2]);
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
	public ArrayList<CarOrder> getPendingOrders(User user) throws UserAccessException {
		this.checkUser(user, "getPendingOrders");
		return GetOrdersWithStatus(user,false);
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
	public ArrayList<CarOrder> getCompletedOrders(User user) throws UserAccessException {
		this.checkUser(user, "getCompletedOrders");
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
	private ArrayList<CarOrder> GetOrdersWithStatus(User user, boolean b) throws UserAccessException {
		ArrayList<CarOrder> result = new ArrayList<CarOrder>();
		for(CarOrder i : this.getOrders(user)){
			if(i.isCompleted().equals(b)) result.add(i);
		}
		return result;
	}
	
	/**
	 * Checks if the give user can perform the given method (defined by a string). 
	 * 
	 * @param	user
	 * 			The user that wants to call the given method.
	 * @param	methodString
	 * 			The string that defines the method.
	 * @throws	UserAccessException
	 *			If the user is not authorized to call the given method.
	 */
	private void checkUser(User user, String methodString) throws UserAccessException {
		if (!user.canPerform(methodString))
			throw new UserAccessException(user, methodString);
	}
}

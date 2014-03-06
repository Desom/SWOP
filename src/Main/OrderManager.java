package Main;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;



public class OrderManager {

	private ProductionSchedule productionSchedule;
	private final HashMap<Integer,ArrayList<CarOrder>> carOrdersPerId;

	/**
	 * Constructor for the OrderManager class.
	 * This constructor is also responsible for creating objects for all the placed carOrders.
	 * This constructor is also responsible for creating a ProductionSchedule and feeding it the unfinished carOrders.
	 * 
	 * @param 	catalog
	 * 			The CarModelCatalog necessary for finding the Options and CarModel Objects of all CarOrders
	 */
	public OrderManager(CarModelCatalog catalog) {
		ArrayList<CarOrder> allCarOrders = this.createOrderList(catalog);
		this.carOrdersPerId = new HashMap<Integer,ArrayList<CarOrder>>();
		for(CarOrder order : allCarOrders) {
			this.addCarOrder(order);
		}
		
		this.createProductionSchedule(allCarOrders);
	}
	
	/**
	 * Give a list of all the CarOrders placed by a given user.
	 * 
	 * @param 	user
	 * 			The User that wants to call this method.
	 * 			The User whose CarOrders are requested.
	 * @return	A copy of the list of all CarOrders made by the given user.
	 * @throws	UserAccessException 
	 * 			If the user is not authorized to call the given method.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<CarOrder> getOrders(User user) throws UserAccessException{
		if(user.canPerform("getOrders"))
		{
			return (ArrayList<CarOrder>) this.getCarOrdersPerId().get(user.getId()).clone();
		}
		else
		{
			throw new UserAccessException(user, "completionEstimate");
		}
	}
	
	/**
	 * Create a carOrder based on the user,model and options and put it in a ProductionSchedule.
	 * 
	 * @param 	user
	 * 			The User that wants to call this method.
	 * 			The User that wants to place a CarOrder.
	 * @param 	model
	 * 			The chosen CarModel of the ordered Car.
	 * @param 	options
	 * 			The chosen Options of the ordered Car.
	 * @throws UserAccessException
	 * 			If the user is not authorized to call the given method.
	 */
	public void placeOrder(User user, CarModel model, ArrayList<Option> options) throws UserAccessException{
		if(user.canPerform("placeOrder"))
		{
			CarOrder newOrder = new CarOrder(user,model,options);
			this.addCarOrder(newOrder);
			this.getProductionSchedule().addOrder(newOrder);
		}
		else
		{
			throw new UserAccessException(user, "completionEstimate");
		}
	}
	
	/**
	 * Calculates an estimated completion date for a specific CarOrder and returns it.
	 * 
	 * @param 	user
	 * 			The User that wants to call this method.
	 * @param 	order
	 * 			The CarOrder whose estimated completion date is requested.
	 * @return	A GregorianCalendar representing the estimated completion date of order.
	 * @throws 	UserAccessException
	 * 			If the user is not authorized to call the given method.
	 */
	public GregorianCalendar completionEstimate(User user, CarOrder order) throws UserAccessException{
		if(user.canPerform("completionEstimate"))
		{
			return this.getProductionSchedule().completionEstimateCarOrder(order);
		}
		else
		{
			throw new UserAccessException(user, "completionEstimate");
		}
	}

	/**
	 * Creates a ProductionSchedule which is initialised with the given CarOrders.
	 * 
	 * @param orderList
	 * 			The list of orders that has to be scheduled on the create ProductionSchedule.
	 */
	private void createProductionSchedule(ArrayList<CarOrder> orderList){
		ProductionSchedule newProductionSchedule = new ProductionSchedule(orderList);
		this.setProductionSchedule(newProductionSchedule);
	}
	
	/**
	 * Creates all the placed CarOrders.
	 * @param 	catalog
	 * 			The CarModelCatalog used to convert Strings to Option and CarModel objects.
	 * @return	A list of all the placed CarOrders.
	 */
	private ArrayList<CarOrder> createOrderList(CarModelCatalog catalog){
		ArrayList<CarOrder> allCarOrders = new ArrayList<CarOrder>();
		ArrayList<String> allCarOrderInfo = new ArrayList<String>();
		try {
			FileInputStream fStream = new FileInputStream("carOrderData.txt");
			DataInputStream dinStream = new DataInputStream(fStream);
			InputStreamReader insReader = new InputStreamReader(dinStream);
			BufferedReader bReader = new BufferedReader(insReader);
			bReader.readLine();
			String otherLine = bReader.readLine();
			while(!otherLine.startsWith("End")){
				allCarOrderInfo.add(otherLine);
				otherLine = bReader.readLine();
			}
			//TODO is multiple exceptions in 1 catcher possible?
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		
		for(String orderStr: allCarOrderInfo){
			String[] orderPieces = orderStr.split("....");
			// String omvormen naar objecten
		// 0 : orderId
			int orderId = Integer.parseInt(orderPieces[0]);
		// 1 : garageHolderId
			int garageHolderId = Integer.parseInt(orderPieces[1]);
		// 2 : isDelivered -> Boolean
			boolean isDelivered = false;
			if(orderPieces[2] == "1"){
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
			String[] optionStr = orderPieces[6].split("{&}");
			for(String optionDescr: optionStr){
				optionsList.add(catalog.getOption(optionDescr));
			}
			
			allCarOrders.add(new CarOrder(orderId, garageHolderId, orderedCalendar, deliveredCalendar, model, optionsList));
		}
		
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
		String[] dateTime = info.split("*");
		String[] dateStr = dateTime[0].split("-");
		String[] timeStr = dateTime[0].split(":");
		int[] dateInt = new int[3];
		int[] timeInt = new int[3];
		for(int i = 0; i < 3;i++){
			dateInt[i] = Integer.parseInt(dateStr[i]);
			timeInt[i] = Integer.parseInt(timeStr[i]);
		}
		return new GregorianCalendar(dateInt[0],dateInt[1],dateInt[2],timeInt[0],timeInt[1],timeInt[2]);
	}

	
	/**
	 * Add a new CarOrder to the OrderManager 
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

	protected ProductionSchedule getProductionSchedule() {
		return productionSchedule;
	}

	private void setProductionSchedule(ProductionSchedule productionSchedule) {
		this.productionSchedule = productionSchedule;
	}

	private HashMap<Integer, ArrayList<CarOrder>> getCarOrdersPerId() {
		return carOrdersPerId;
	}

}

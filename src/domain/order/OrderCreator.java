package domain.order;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.configuration.VehicleModel;
import domain.configuration.VehicleModelCatalog;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.policies.InvalidConfigurationException;
import domain.policies.Policy;
import domain.user.CustomShopManager;
import domain.user.GarageHolder;

//TODO interface
//TODO mss beide create..Order methodes wat refactoren? (want veel overlappende code)
public class OrderCreator {

	private String path;
	private VehicleModelCatalog catalog;
	private ArrayList<Policy> policyChains;

	/**
	 * Constructor for the OrderCreator class
	 * 
	 * @param path
	 * 		The path to the file with  orders.
	 * @param  catalog
	 * 		The  model catalog used to convert Strings to Option and Model objects.
	 * @param policyChain
	 * 		The chains of policies that checks the validness of the configuration.
	 * 		1st chain : VehicleOrders
	 * 		2nd chain : SingleTaskOrders
	 */
	public OrderCreator(String path, VehicleModelCatalog catalog, ArrayList<Policy> policyChains){
		this.path = path;
		this.catalog = catalog;
		this.policyChains = policyChains;
	}

	/**
	 * Creates all placed Orders.
	 * 
	 * @return a list of all placed Orders.
	 * @throws InvalidConfigurationException
	 * 		If the configuration is invalid.
	 * @throws IOException
	 * 		If a model can't be found.
	 */
	public ArrayList<Order> createOrderList() throws IOException{
		ArrayList<Order> allOrders = new ArrayList<Order>();
		ArrayList<String> allOrderInfo = new ArrayList<String>();
		FileInputStream fStream = new FileInputStream(this.path);
		DataInputStream dinStream = new DataInputStream(fStream);
		InputStreamReader insReader = new InputStreamReader(dinStream);
		BufferedReader bReader = new BufferedReader(insReader);

		String otherLine = bReader.readLine();
		while(!otherLine.startsWith("Begin")){
			otherLine = bReader.readLine();
		}

		otherLine = bReader.readLine();
		while(!otherLine.startsWith("End")){
			allOrderInfo.add(otherLine);
			otherLine = bReader.readLine();
		}
		bReader.close();

		try{
			for(String orderInfo: allOrderInfo){
				String[] orderPieces = orderInfo.split(",,,,");
				int kindOfOrder = Integer.parseInt(orderPieces[0]);

				switch (kindOfOrder) {
				case 0 :
					allOrders.add(this.createVehicleOrder(orderPieces, policyChains.get(0)));
					break;
				case 1 :
					allOrders.add(this.createSingleTaskOrder(orderPieces, policyChains.get(1)));
					break;
				}
			}
		}
		catch(NumberFormatException e){
			throw new IOException("The file had incorrect information or wrong was in the wrong format.");
		}
		return allOrders;
	}

	private VehicleOrder createVehicleOrder(String[] orderPieces, Policy policyChain) throws IOException{

		// Strings omvormen naar objecten
		// 1 : OrderId
		int OrderId = Integer.parseInt(orderPieces[1]);
		// 2 : garageHolderId
		GarageHolder garageHolder = new GarageHolder(Integer.parseInt(orderPieces[2]));
		// 3 : isDelivered -> Boolean
		boolean isDelivered = false;
		if(orderPieces[3].equals("1")){
			isDelivered = true;
		}

		// 4 : orderedTime -> GregorianCalendar
		GregorianCalendar orderedCalendar = this.createCalendarFor(orderPieces[4]);
		// 5 : deliveryTime -> GregorianCalendar
		GregorianCalendar deliveredCalendar = null;
		if(isDelivered){
			deliveredCalendar = this.createCalendarFor(orderPieces[5]);
		}
		// 6 : modelId -> Model (we hebben hiervoor de this.catalog nodig, hoe komen we daar aan?)
		VehicleModel model = findModel(orderPieces[6]);
		// 7 : options -> ArrayList<Option> (ook this.catalog nodig)
		ArrayList<Option> optionsList = findOptions(orderPieces[7]);
		Configuration config = new Configuration(model, policyChain);
		try{
			for(Option i: optionsList){
				config.addOption(i);
			}
			config.complete();
		}
		catch(InvalidConfigurationException e){
			throw new IOException("The file contains an order whose configuration wasn't conform with the policies.");
		}
		return new VehicleOrder(OrderId, garageHolder, config, orderedCalendar, deliveredCalendar, isDelivered);


	}


	private SingleTaskOrder createSingleTaskOrder(String[] orderPieces, Policy policyChain) throws IOException {

		// Strings omvormen naar objecten
		// 1 : OrderId
		int OrderId = Integer.parseInt(orderPieces[1]);
		// 2 : garageHolderId
		CustomShopManager customShopManager = new CustomShopManager(Integer.parseInt(orderPieces[2]));
		// 3 : isDelivered -> Boolean
		boolean isDelivered = false;
		if(orderPieces[3].equals("1")){
			isDelivered = true;
		}

		// 4 : orderedTime -> GregorianCalendar
		GregorianCalendar orderedCalendar = this.createCalendarFor(orderPieces[4]);
		// 5 : deliveryTime -> GregorianCalendar
		GregorianCalendar deliveredCalendar = null;
		if(isDelivered){
			deliveredCalendar = this.createCalendarFor(orderPieces[5]);
		}

		// 6 : option -> Configuration
		Option option = findOptions(orderPieces[6]).get(0);
		Configuration config = new Configuration(null, policyChain);
		try{
			config.addOption(option);
			config.complete();
		}
		catch(InvalidConfigurationException e){
			throw new IOException("The file contained an order whose configuration wasn't conform with the policies.");
		}
		// 7 : deadlineTime -> GregorianCalendar
		GregorianCalendar deadlineCalendar = this.createCalendarFor(orderPieces[7]);

		return new SingleTaskOrder(OrderId, customShopManager, config, deadlineCalendar, orderedCalendar, deliveredCalendar, isDelivered);


	}

	/**
	 * Finds  options associated with the given string.
	 * 
	 * @param optionsString
	 * 		The string indicating the the options.
	 * @return The options associated with the given options string.
	 * @throws IOException
	 * 		When there are conflicting options. 
	 */
	private ArrayList<Option> findOptions(String optionsString) throws IOException {
		ArrayList<Option> optionsList = new ArrayList<Option>();
		String[] optionStr = optionsString.split(";-;");
		for(String optionDescr: optionStr){
			Option found = null;
			for(Option option : this.catalog.getAllOptions()){
				if(option.getDescription().equals(optionDescr)){
					found = option;
					break;
				}
			}
			if(found!=null){
				optionsList.add(found);				
			}else{
				throw new IOException("The file contains an option that doesn't exist. named: " + optionDescr);
			}
		}

		for(Option option : optionsList){
			for(Option other: optionsList){
				if(option != other && option.conflictsWith(other))
					throw new IOException("The file contained Orders with conflicting Options. file: " + path);
			}
		}
		return optionsList;
	}

	/**
	 * Finds the  model, using the given name.
	 * 
	 * @param ModelName
	 * 		The name of the desired  model.
	 * @return The model associated with the given name.
	 * @throws IOException
	 * 		If no  model can be found.
	 */
	private VehicleModel findModel(String ModelName) throws IOException {
		for(VehicleModel model : this.catalog.getAllModels()){
			if(model.getName().equals(ModelName))
				return model;
		}
		throw new IOException("The desired  model doesn't exist");
	}

	/**
	 * Create a GregorianCalendar based on the given time and date.
	 * 
	 * @param info
	 * 		The String that has to be converted to a GregorianCalendar object; format=DD-MM-YYYY*HH:MM:SS
	 * @return A GregorianCalendar based on the given time and date.
	 * @throws IOException
	 * 		If info isn't in the correct format.
	 */
	private GregorianCalendar createCalendarFor(String info) throws IOException {
		String[] dateTime = info.split("==");
		String[] dateStr = dateTime[0].split("-");
		String[] timeStr = dateTime[1].split("-");
		int[] dateInt = new int[3];
		int[] timeInt = new int[3];
		try{
			for(int i = 0; i < 3;i++){
				dateInt[i] = Integer.parseInt(dateStr[i]);
				timeInt[i] = Integer.parseInt(timeStr[i]);
			}
		}
		catch(NumberFormatException e){
			throw new IOException("The given date isn't in the correct format.");
		}
		return new GregorianCalendar(dateInt[2],dateInt[1],dateInt[0],timeInt[0],timeInt[1],timeInt[2]);
	}
}

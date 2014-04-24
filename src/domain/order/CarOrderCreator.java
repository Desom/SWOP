package domain.order;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.policies.InvalidConfigurationException;
import domain.policies.Policy;
import domain.user.GarageHolder;

public class CarOrderCreator {
	
	private String path;
	private CarModelCatalog catalog;
	private Policy policyChain;
	
	/**
	 * Constructor for the CarOrderCreator class
	 * 
	 * @param path
	 * 		The path to the file with car orders.
	 * @param  catalog
	 * 		The car model catalog used to convert Strings to Option and CarModel objects.
	 * @param policyChain
	 * 		The chain of policies that checks the validness of the configuration.
	 */
	public CarOrderCreator(String path, CarModelCatalog catalog, Policy policyChain){
		this.path = path;
		this.catalog = catalog;
		this.policyChain = policyChain;
	}
	
	/**
	 * Creates all placed CarOrders.
	 * 
	 * @return a list of all placed CarOrders.
	 * @throws InvalidConfigurationException
	 * 		If the configuration is invalid.
	 * @throws IOException
	 * 		If a car model can't be found.
	 */
	public ArrayList<Order> createCarOrderList() throws InvalidConfigurationException, IOException{
		ArrayList<Order> allCarOrders = new ArrayList<Order>();
		ArrayList<String> allCarOrderInfo = new ArrayList<String>();
		try {
			FileInputStream fStream = new FileInputStream(this.path);
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
		
		for(String orderStr: allCarOrderInfo){
			String[] orderPieces = orderStr.split(",,,,");
			// String omvormen naar objecten
		// 0 : carOrderId
			int carOrderId = Integer.parseInt(orderPieces[0]);
		// 1 : garageHolderId
			GarageHolder garageHolder = new GarageHolder(Integer.parseInt(orderPieces[1]));
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
		// 5 : modelId -> CarModel (we hebben hiervoor de this.catalog nodig, hoe komen we daar aan?)
			CarModel model = findCarModel(orderPieces[5]);
		// 6 : options -> ArrayList<Option> (ook this.catalog nodig)
			ArrayList<Option> optionsList = findCarOptons(orderPieces[6]);
			Configuration config = new Configuration(model, policyChain);
			for(Option i: optionsList){
				config.addOption(i);
			}
			config.complete();
			allCarOrders.add(new CarOrder(carOrderId, garageHolder, config, orderedCalendar, deliveredCalendar, isDelivered));
		}
		
		return allCarOrders;
	}

	/**
	 * Finds car options associated with the given string.
	 * 
	 * @param optionsString
	 * 		The string indicating the the options.
	 * @return The options associated with the given options string.
	 */
	private ArrayList<Option> findCarOptons(String optionsString) {
		ArrayList<Option> optionsList = new ArrayList<Option>();
		String[] optionStr = optionsString.split(";-;");
		for(String optionDescr: optionStr){
			for(Option option : this.catalog.getAllOptions()){
				if(option.getDescription().equals(optionDescr)){
					optionsList.add(option);
					continue;
				}
			}
		}
		for(Option option : optionsList){
			for(Option other: optionsList){
				if(option != other && option.conflictsWith(other))
					throw new IllegalArgumentException("There are conflicting options");
			}
		}
		return optionsList;
	}

	/**
	 * Finds the car model, using the given name.
	 * 
	 * @param carModelName
	 * 		The name of the desired car model.
	 * @return The model associated with the given name.
	 * @throws IOException
	 * 		If no car model can be found.
	 */
	private CarModel findCarModel(String carModelName) throws IOException {
		for(CarModel model : this.catalog.getAllModels()){
			if(model.getName().equals(carModelName))
				return model;
		}
		throw new IOException("The desired car model doesn't exist");
	}

	/**
	 * Create a GregorianCalendar based on the given time and date.
	 * 
	 * @param info
	 * 		The String that has to be converted to a GregorianCalendar object; format=DD-MM-YYYY*HH:MM:SS
	 * @return A GregorianCalendar based on the given time and date.
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
}

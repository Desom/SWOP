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

public class CarOrderCreator {
	//TODO is dit conform met het Factory pattern ???
	
	
	private String path;
	private CarModelCatalog catalog;
	private Policy policy;
	/**
	 * Constructor for the CarOrderCreator class
	 * @param carOrderPolicy 
	 * @param	dataFile
	 * 			The file from which the CarOrders will be read.
	 * @param 	this.catalog
	 * 			The CarModelthis.catalog used to convert Strings to Option and CarModel objects.
	 */
	public CarOrderCreator(String path, CarModelCatalog catalog, Policy carOrderPolicy){
		this.path = path;
		this.catalog = catalog;
		this.policy = carOrderPolicy;
	}
	
	/**
	 * Creates all the placed CarOrders.
	 * 
	 * @return	A list of all the placed CarOrders.
	 * @throws InvalidConfigurationException 
	 */
	public ArrayList<CarOrder> createCarOrderList() throws InvalidConfigurationException{
		ArrayList<CarOrder> allCarOrders = new ArrayList<CarOrder>();
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
		} catch (IOException e) {//TODO deze exception doorgooien om aan de gebruiker te kunnen laten weten?
			return null;
		}
		
		for(String orderStr: allCarOrderInfo){
			String[] orderPieces = orderStr.split(",,,,");
			// String omvormen naar objecten
		// 0 : carOrderId
			int carOrderId = Integer.parseInt(orderPieces[0]);
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
		// 5 : modelId -> CarModel (we hebben hiervoor de this.catalog nodig, hoe komen we daar aan?)
			CarModel model = findCarModel(orderPieces[5]);
		// 6 : options -> ArrayList<Option> (ook this.catalog nodig)
			ArrayList<Option> optionsList = findCarOptons(orderPieces[6]);
			Configuration config = new Configuration(model, policy);
			for(Option i: optionsList){
				config.setOption(i);
			}
			allCarOrders.add(new CarOrder(carOrderId, garageHolderId, orderedCalendar, deliveredCalendar, config));
		}
		
		return allCarOrders;
	}

	/**
	 * @param this.catalog
	 * @param orderPieces
	 * @return
	 */
	private ArrayList<Option> findCarOptons(String orderPiece) {
		ArrayList<Option> optionsList = new ArrayList<Option>();
		String[] optionStr = orderPiece.split(";-;");
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
					throw new IllegalArgumentException("There are conflicting options"); // TODO welke exception is beter?
			}
		}
		return optionsList;
	}

	/**
	 * @param this.catalog
	 * @param orderPieces
	 * @return
	 */
	private CarModel findCarModel(String orderPiece) {
		for(CarModel model : this.catalog.getAllModels()){
			if(model.getName().equals(orderPiece))
				return model;
		}
		return null; // TODO error gooien, want slechte file? welke? IOException?
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
}

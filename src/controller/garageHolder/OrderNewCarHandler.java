package controller.garageHolder;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import controller.OurOrderform;
import controller.UIInterface;
import domain.Company;
import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.user.GarageHolder;

public class OrderNewCarHandler {

	public void run(UIInterface ui, Company company, GarageHolder garageHolder) {
		//TODO
	}
	
	/**
	 * Get a car model based on the name
	 * @param name the name
	 * @return a car model based with the name name
	 * 	       null if the name does not match a model 
	 */
	private CarModel getCarModel(String name, Company company){
		CarModelCatalog catalog = company.getCatalog();
		for(CarModel possible: catalog.getAllModels()){
			if(possible.getName().equals(name)) return possible;
		}
		return null;
	}
}

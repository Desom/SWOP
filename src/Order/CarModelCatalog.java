package Order;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Car.CarModel;
import Car.Option;
import Car.OptionType;
import User.UserAccessException;



public class CarModelCatalog {
	private ArrayList<Option> All_Options;
	private ArrayList<CarModel> All_CarModels;

	/**
	 * Create a carmodelcatalog
	 * @param Optionfile this file is the file which contains the data of options
	 * @param Modelfile this file is the file which contains the data of models
	 * @throws IOException 
	 * @throws CarModelCatalogException
	 */
	public CarModelCatalog(String Optionfile, String Modelfile) throws IOException, CarModelCatalogException{
		
		
		All_Options = (new OptionCreator()).createOptions();
		All_CarModels= (new modelCreator(this.getOptions())).createModels();
		
	}
	public CarModelCatalog() throws IOException, CarModelCatalogException{
		this("options.txt", "models.txt");
	}
	
	/***
	 * get the list of all models
	 * @param user the user who wants to access the models
	 * @return a list of all the models
	 * @throws UserAccessException the use has no authority to view the model
	 */
	@SuppressWarnings("unchecked")
	public List<CarModel> getAllModels()  {
	return  (List<CarModel>) All_CarModels.clone();
		
	}
	@SuppressWarnings("unchecked")
	public List<Option> getOptions() {
		return (List<Option>) (All_Options.clone());
	}

	
	
}

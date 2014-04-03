package Order;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Car.CarModel;
import Car.OptionType;



public class CarModelCatalog {
	private ArrayList<Option> allOptions;
	private ArrayList<CarModel> allCarModels;

	/**
	 * Create a CarModelCatalog
	 * @param optionfile this file is the file which contains the data of options
	 * @param modelFile this file is the file which contains the data of models
	 * @throws IOException 
	 * @throws CarModelCatalogException
	 */
	public CarModelCatalog(String optionfile, String modelFile) throws IOException, CarModelCatalogException{


		allOptions = (new OptionCreator()).createOptions();
		allCarModels= (new ModelCreator(this.getAllOptions())).createModels();

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
		return  (List<CarModel>) allCarModels.clone();

	}
	@SuppressWarnings("unchecked")
	public List<Option> getAllOptions() {
		return (List<Option>) (allOptions.clone());
	}



}

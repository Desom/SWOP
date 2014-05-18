package domain.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VehicleModelCatalog {
	
	private ArrayList<Option> allOptions;
	private ArrayList<VehicleModel> allModels;
	public static TaskTypeCreator taskTypeCreator = new TaskTypeCreator();

	/**
	 * Constructor of VehicleModelCatalog.
	 * 
	 * @throws IOException
	 * @throws VehicleModelCatalogException
	 */
	public VehicleModelCatalog() throws IOException, VehicleModelCatalogException{
		allOptions = (new OptionCreator()).createOptions();
		allModels= (new ModelCreator(this.getAllOptions())).createModels();
	}

	/**
	 * Constructor of VehicleModelCatalog.
	 * 
	 * @param optionPath
	 * 		The path to the file containing the options.
	 * @param dependancyPath
	 * 		The path to the file containing the dependencies.
	 * @param modelPath
	 * 		The path to the file containing the models.
	 * @throws IOException
	 * @throws VehicleModelCatalogException
	 */
	public VehicleModelCatalog(String optionPath, String dependancyPath, String modelPath) throws IOException, VehicleModelCatalogException{
		allOptions = (new OptionCreator(optionPath,dependancyPath)).createOptions();
		allModels= (new ModelCreator(this.getAllOptions(),modelPath)).createModels();
	}

	/**
	 * Gets the list of all models.
	 * 
	 * @return the list of all models
	 */
	@SuppressWarnings("unchecked")
	public List<VehicleModel> getAllModels()  {
		return  (List<VehicleModel>) allModels.clone();

	}
	
	/**
	 * Gets the list of all options.
	 * 
	 * @return the list of all options
	 */
	@SuppressWarnings("unchecked")
	public List<Option> getAllOptions() {
		return (List<Option>) (allOptions.clone());
	}



}

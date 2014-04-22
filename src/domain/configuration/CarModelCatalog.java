package domain.configuration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CarModelCatalog {
	
	private ArrayList<Option> allOptions;
	private ArrayList<CarModel> allCarModels;

	/**
	 * Constructor of CarModelCatalog.
	 * 
	 * @param optionFile
	 * 			The file which contains the data of options
	 * @param modelFile
	 * 		The file which contains the data of models
	 * @throws IOException
	 * @throws CarModelCatalogException
	 */
	public CarModelCatalog(String optionFile, String dependancyPath, String modelFile) throws IOException, CarModelCatalogException{
		allOptions = (new OptionCreator(optionFile,dependancyPath)).createOptions();
		allCarModels= (new ModelCreator(this.getAllOptions(),modelFile)).createModels();
	}
	
	/**
	 * Constructor of CarModelCatalog.
	 * 
	 * @throws IOException
	 * @throws CarModelCatalogException
	 */
	public CarModelCatalog() throws IOException, CarModelCatalogException{
		allOptions = (new OptionCreator()).createOptions();
		allCarModels= (new ModelCreator(this.getAllOptions())).createModels();
	}

	/***
	 * Gets the list of all models.
	 * 
	 * @param user
	 * 		The user who wants to access the models
	 * @return the list of all models
	 */
	@SuppressWarnings("unchecked")
	public List<CarModel> getAllModels()  {
		return  (List<CarModel>) allCarModels.clone();

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

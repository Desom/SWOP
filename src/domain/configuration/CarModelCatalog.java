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
	 * @throws IOException
	 * @throws CarModelCatalogException
	 */
	public CarModelCatalog() throws IOException, CarModelCatalogException{
		allOptions = (new OptionCreator()).createOptions();
		allCarModels= (new ModelCreator(this.getAllOptions())).createModels();
	}

	/**
	 * Constructor of CarModelCatalog.
	 * 
	 * @param optionPath
	 * 		The path to the file containing the options.
	 * @param dependancyPath
	 * 		The path to the file containing the dependencies.
	 * @param modelPath
	 * 		The path to the file containing the models.
	 * @throws IOException
	 * @throws CarModelCatalogException
	 */
	public CarModelCatalog(String optionPath, String dependancyPath, String modelPath) throws IOException, CarModelCatalogException{
		allOptions = (new OptionCreator(optionPath,dependancyPath)).createOptions();
		allCarModels= (new ModelCreator(this.getAllOptions(),modelPath)).createModels();
	}

	/**
	 * Gets the list of all models.
	 * 
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

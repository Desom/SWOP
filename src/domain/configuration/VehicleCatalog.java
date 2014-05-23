package domain.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import domain.assembly.workstations.WorkstationTypeCreatorInterface;

public class VehicleCatalog {
	
	private ArrayList<Option> allOptions;
	private ArrayList<Part> allParts;
	private ArrayList<VehicleModel> allModels;
	private final WorkstationTypeCreatorInterface workstationTypeCreator;
	public static AbstractTaskTypeCreator taskTypeCreator = new TaskTypeCreator();

	/**
	 * Constructor of VehicleModelCatalog.
	 * 
	 * @throws IOException
	 * @throws VehicleCatalogException
	 */
	public VehicleCatalog(WorkstationTypeCreatorInterface workstationTypeCreator) throws IOException, VehicleCatalogException{
		OptionCreatorInterface creator = new OptionCreator();
		this.workstationTypeCreator = workstationTypeCreator;
		allOptions = creator.createOptions();
		allParts = creator.createParts();
		allModels= (new ModelCreator(this.workstationTypeCreator, this.getAllOptions(),this.getAllParts())).createModels();
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
	 * @throws VehicleCatalogException
	 */
	public VehicleCatalog(WorkstationTypeCreatorInterface workstationTypeCreator, String optionPath, String dependancyPath, String modelPath) throws IOException, VehicleCatalogException{
		this.workstationTypeCreator = workstationTypeCreator;
		OptionCreatorInterface oCreator = new OptionCreator(optionPath,dependancyPath);
		allOptions = oCreator.createOptions();
		allParts = oCreator.createParts();
		allModels= (new ModelCreator(this.workstationTypeCreator, this.getAllOptions(), this.getAllParts(), modelPath)).createModels();
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
	
	/**
	 * Gets the list of all parts.
	 * 
	 * @return the list of all parts
	 */
	@SuppressWarnings("unchecked")
	public List<Part> getAllParts() {
		return (List<Part>) (allParts.clone());
	}


}

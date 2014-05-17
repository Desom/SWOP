package domain.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ModelCreator {
	
	private String path;
	private List<Option> options;
	private HashMap<String, VehicleModel> allModels;
	
	/**
	 * Constructor of ModelCreator.
	 * 
	 * @param options
	 * 		All possible options that can be associated with a vehicle model.
	 * 		In other words, all possible options that could be given to a configuration.
	 * @param path
	 * 		The path to the file containing the vehicle models.
	 */
	public ModelCreator(List<Option> options, String path){
		this.options = options;
		this.path = path;
	}

	/**
	 * Constructor of ModelCreator.
	 * Uses the default path the the file containing the vehicle models.
	 * 
	 * @param options
	 * 		All possible options that can be associated with a vehicle model.
	 * 		In other words, all possible options that could be given to a configuration.
	 */
	public ModelCreator(List<Option> options){
		this(options, "data/models.txt");
	}

	/**
	 * Creates the models from the file of the path of this model creator.
	 * 
	 * @return a list with all possible vehicle models
	 * @throws IOException
	 * @throws VehicleModelCatalogException
	 * 		If a model line is not in the right format.
	 */
	public ArrayList<VehicleModel> createModels() throws IOException, VehicleModelCatalogException{
		this.allModels = new HashMap<String,VehicleModel>();
		BufferedReader input = new BufferedReader(new FileReader(path));
		String inputline = input.readLine();
		while(inputline != null){
			processModelLine(inputline);
			inputline = input.readLine();
		}
		input.close();
		return new ArrayList<VehicleModel>(allModels.values());
	}
	
	/**
	 * Processes a line of the vehicle model file.
	 * 
	 * @param inputline
	 * 		A line of the vehicle model file.
	 * @throws VehicleModelCatalogException 
	 * 		If the line is in the wrong format.
	 * 		If the model name already exists.
	 * 		If an option type is missing.
	 */
	private void processModelLine(String inputline) throws VehicleModelCatalogException {
		// Determine default task time
		int defaultTaskTime = 60;
		if(inputline.contains("%")){
			String[] original = inputline.split("%");
			defaultTaskTime = Integer.parseInt(original[1]);
			inputline = original[0];
		}
		// continue loading the options
		String[] input=inputline.split(";");
		if(input.length != 2) throw new VehicleModelCatalogException("Model: wrong input format: " + inputline);
		if(allModels.containsKey(input[0])) throw new VehicleModelCatalogException("Model name already exists: "+input[0] );
		try{
			ArrayList<String> a = new ArrayList<String>(Arrays.asList(input[1].split(",")));
			allModels.put(input[0], new VehicleModel(input[0], collectOption(a), defaultTaskTime));
		}catch(ClassCastException e){
			throw new VehicleModelCatalogException("Wrong Option Type in form: " + inputline);
		}
	}

	/**
	 * Makes a list of options made associated with the given list of option descriptions.
	 * 
	 * @param descriptions 
	 * 		The list of option descriptions.
	 * @return a list of options associated with the given descriptions
	 * @throws VehicleModelCatalogException
	 * 		If there is no option with one of the given descriptions.
	 */
	private ArrayList<Option> collectOption(ArrayList<String> descriptions) throws VehicleModelCatalogException {
		ArrayList<Option> options = new ArrayList<Option>();
		for(String description : descriptions){
			Option option = getOption(description);
			if(option != null)
				options.add(option);
			else
				throw new VehicleModelCatalogException("Option does not exists: "+ description);
		}
		return options;
	}

	/**
	 * Returns the option associated with the option description. If there is none, null is returned.
	 * 
	 * @param optionDescription
	 * 		The description of the option.
	 * @return the options associated with the option name, or null when there is none
	 */
	private Option getOption(String optionDescription) {
		for(Option option : options){
			if(option.getDescription().equals(optionDescription))
				return option;
		}
		return null;
	}
}

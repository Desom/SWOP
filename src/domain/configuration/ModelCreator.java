package domain.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import domain.assembly.workstations.WorkstationType;
import domain.assembly.workstations.WorkstationTypeCreatorInterface;

public class ModelCreator implements ModelCreatorInterface {
	
	private String path;
	private List<Option> options;
	private List<Part> parts;
	private HashMap<String, VehicleModel> allModels;
	private final WorkstationTypeCreatorInterface workstationTypeCreator;
	
	/**
	 * Constructor of ModelCreator.
	 * @param creator
	 * 		The assemblyLineCreator to be used to gather the workstationTypes.
	 * @param options
	 * 		All possible options that can be associated with a vehicle model.
	 * 		In other words, all possible options that could be given to a configuration.
	 * @param parts
	 * 		All possible parts that can be associated with a vehicle model.
	 * @param path
	 * 		The path to the file containing the vehicle models.
	 */
	public ModelCreator(WorkstationTypeCreatorInterface creator, List<Option> options, List<Part> parts, String path){
		this.options = options;
		this.parts = parts;
		this.path = path;
		this.workstationTypeCreator = creator;
	}

	/**
	 * Constructor of ModelCreator.
	 * Uses the default path the the file containing the vehicle models.
	 * @param creator
	 * 		The assemblyLineCreator to be used to gather the workstationTypes.
	 * @param options
	 * 		All possible options that can be associated with a vehicle model.
	 * 		In other words, all possible options that could be given to a configuration.
	 * @param parts
	 * 		All possible parts that can be associated with a vehicle model.
	 */
	public ModelCreator(WorkstationTypeCreatorInterface creator, List<Option> options, List<Part> parts){
		this(creator, options, parts, "data/models.txt");
	}

	@Override
	public ArrayList<VehicleModel> createModels() throws IOException, VehicleCatalogException{
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
	 * @throws VehicleCatalogException 
	 * 		If the line is in the wrong format.
	 * 		If the model name already exists.
	 * 		If an option type is missing.
	 */
	private void processModelLine(String inputline) throws VehicleCatalogException {
		
		ArrayList<String> parts = new ArrayList<String>();
		// determine if the model needs parts.
		if(inputline.contains("!")){
			String[] original = inputline.split("!");
			parts = new ArrayList<String>(Arrays.asList(original[1].split(",")));
			inputline = original[0];
		}
		
		// Determine default task time
		HashMap<WorkstationType, Integer> workstationTimes = new HashMap<WorkstationType, Integer>();
		if(inputline.contains("%")){
			String[] original = inputline.split("%");
			
			try{
				int TaskTime = Integer.parseInt(original[1]);
				for(WorkstationType t : workstationTypeCreator.getAllWorkstationTypes()){
					workstationTimes.put(t, TaskTime);
				}
			}catch(NumberFormatException e){
				for(WorkstationType t : workstationTypeCreator.getAllWorkstationTypes()){
					workstationTimes.put(t, 60);
				}
				HashMap<WorkstationType, Integer> parsedTimes = parseTimes(original[1]);
				for(WorkstationType t : parsedTimes.keySet()){
					workstationTimes.put(t, parsedTimes.get(t));
				}
			}
			
			inputline = original[0];
		}else{
			for(WorkstationType t : workstationTypeCreator.getAllWorkstationTypes()){
				workstationTimes.put(t, 60);
			}
		}
		// continue loading the options
		String[] input=inputline.split(";");
		if(input.length != 2) throw new VehicleCatalogException("Model: wrong input format: " + inputline);
		if(allModels.containsKey(input[0])) throw new VehicleCatalogException("Model name already exists: "+input[0] );
		try{
			ArrayList<String> a = new ArrayList<String>(Arrays.asList(input[1].split(",")));
			allModels.put(input[0], new VehicleModel(input[0], collectOption(a), collectPart(parts), workstationTimes));
		}catch(ClassCastException e){
			throw new VehicleCatalogException("Wrong Option Type in form: " + inputline);
		}
	}

	/**
	 * Makes a list of options made associated with the given list of option descriptions.
	 * 
	 * @param descriptions 
	 * 		The list of option descriptions.
	 * @return a list of options associated with the given descriptions
	 * @throws VehicleCatalogException
	 * 		If there is no option with one of the given descriptions.
	 */
	private ArrayList<Option> collectOption(ArrayList<String> descriptions) throws VehicleCatalogException {
		ArrayList<Option> options = new ArrayList<Option>();
		for(String description : descriptions){
			Option option = getOption(description);
			if(option != null)
				options.add(option);
			else
				throw new VehicleCatalogException("Option does not exists: "+ description);
		}
		return options;
	}
	
	/**
	 * Makes a list of parts made associated with the given list of part descriptions.
	 * 
	 * @param descriptions 
	 * 		The list of part descriptions.
	 * @return a list of parts associated with the given descriptions
	 * @throws VehicleCatalogException
	 * 		If there is no part with one of the given descriptions.
	 */
	private ArrayList<Part> collectPart(ArrayList<String> descriptions) throws VehicleCatalogException {
		ArrayList<Part> parts = new ArrayList<Part>();
		for(String description : descriptions){
			Part part = getPart(description);
			if(part != null)
				parts.add(part);
			else
				throw new VehicleCatalogException("Part does not exists: "+ description);
		}
		return parts;
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
	
	/**
	 * Returns the part associated with the part description. If there is none, null is returned.
	 * 
	 * @param partDescription
	 * 		The description of the part.
	 * @return the part associated with the part name, or null when there is none
	 */
	private Part getPart(String partDescription) {
		for(Part part: parts){
			if(part.getDescription().equals(partDescription))
				return part;
		}
		return null;
	}
	
	/**
	 * Reads the workstation type and time from input lines.
	 * 
	 * @param times
	 * 		Input lines indicating the workstation types and time in a certain format.
	 * @return A hashmap where times are mapped to workstation types.
	 */
	private HashMap<WorkstationType, Integer> parseTimes(String times){
		HashMap<WorkstationType, Integer> timeMap = new HashMap<WorkstationType, Integer>();
		String[] workstations = times.split(",");
		for(String w : workstations){
			String[] data = w.split(":");
			WorkstationType type = null;
			for(WorkstationType t : workstationTypeCreator.getAllWorkstationTypes()){
				if(t.getName().equalsIgnoreCase(data[0].trim())){
					type = t;
				}
			}
			timeMap.put(type, Integer.parseInt(data[1]));
		}
		return timeMap;
	}
}

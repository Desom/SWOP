package domain.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class OptionCreator implements OptionCreatorInterface {

	private HashMap<String,Option> allOptions;
	private HashMap<String,Part> allParts;
	private String optionpath;
	private String dependancypath;

	/**
	 * Constructor of OptionCreator.
	 * Uses the default file paths.
	 */
	public OptionCreator(){
		this("data/options.txt", "data/dependencies.txt");
	}

	/**
	 * Constructor of OptionCreator.
	 * 
	 * @param optionPath
	 * 		The path to the file containing all options.
	 * @param dependancyPath
	 * 		The path to the file containing all dependencies.
	 */
	public OptionCreator(String optionPath,String dependancyPath){
		this.optionpath = optionPath;
		this.dependancypath = dependancyPath;
	}

	@Override
	public ArrayList<Option> createOptions() throws IOException, VehicleCatalogException{
		BufferedReader input = new BufferedReader(new FileReader(optionpath));
		this.allOptions = new HashMap<String,Option>();
		this.allParts = new HashMap<String,Part>();
		String inputline = input.readLine();
		while( inputline!=null){
			if(inputline.equals("!")){
				break;
			}
			processOptionLine(inputline);
			inputline = input.readLine();
		}
		input.close();
		input = new BufferedReader(new FileReader(dependancypath));
		inputline = input.readLine();
		while( inputline!=null){
			processDependencyLine(inputline);
			inputline = input.readLine();
		}
		input.close();
		return new ArrayList<Option>(allOptions.values());
	}
	
	@Override
	public ArrayList<Part> createParts() throws IOException, VehicleCatalogException{
		BufferedReader input = new BufferedReader(new FileReader(optionpath));
		this.allOptions = new HashMap<String,Option>();
		String inputline = input.readLine();
		while( inputline!=null){
			if(inputline.equals("!")){
				inputline = input.readLine();
				break;
			}
			inputline = input.readLine();
		}
		while( inputline!=null){
			processPartLine(inputline);
			inputline = input.readLine();
		}
		input.close();
		
		return new ArrayList<Part>(allParts.values());
	}

	/**
	 * Reads in dependencies from the given input line.
	 * 
	 * @param inputline
	 * 		The input line to be read.
	 * @throws VehicleCatalogException
	 */
	private void processDependencyLine(String inputline) throws VehicleCatalogException {
		String[] input=inputline.split(";");
		if(input.length != 2) throw new VehicleCatalogException("Dependency: wrong input format: " + inputline);
		if(!allOptions.containsKey(input[0])) throw new VehicleCatalogException("Option does not exist: " + input[0]);
		ArrayList<String> optionNames = new ArrayList<String>();
		for(String j:input[1].split(","))optionNames.add(j);
		allOptions.get(input[0]).addDependency(collectOption(optionNames));
	}
	
	/**
	 * Processes a line which stores the information of a single option.
	 * 
	 * @param inputline
	 * 		The line to be processed.
	 * @throws VehicleCatalogException.
	 * 		If the option the line describes already exists.
	 * 		If the line isn't in the right format.
	 * 		If the option the line describes is of the wrong subtype.
	 * 		IF the lines use an option that does not exist.
	 */
	private void processOptionLine(String inputline) throws VehicleCatalogException   {
		String[] input=inputline.split(";");
		if(input.length != 3) throw new VehicleCatalogException("Option: wrong input format: " + inputline);
		if(allOptions.containsKey(input[0])) throw new VehicleCatalogException("Option already exists: " + input[0]);
		ArrayList<String> incomp = new ArrayList<String>(Arrays.asList(input[2].split(",")));
		ArrayList<String> comp = new ArrayList<String>();
		comp.addAll(allOptions.keySet());
		checkAndRemove(comp,incomp);
		sameTypeFilter(comp,incomp,input[1]);
		allOptions.put(input[0], createOption(input[0],input[1], incomp));
	}
	
	/**
	 * Processes a line which stores the information of a single part.
	 * 
	 * @param inputline
	 * 		The line to be processed.
	 * @throws VehicleCatalogException 
	 * 		If the part the line describes already exists
	 * 		If the part isn't in the right format
	 * 		IF the lines use an part that does not exist
	 */
	private void processPartLine(String inputline) throws VehicleCatalogException   {
		String[] input=inputline.split(";");
		if(input.length != 2) throw new VehicleCatalogException("Part: wrong input format: " + inputline);
		if(allParts.containsKey(input[0])) throw new VehicleCatalogException("Part already exists: " + input[0]);
		allParts.put(input[0], createPart(input[0],input[1]));
	}

	/**
	 * Adds all options from the first given list to the second given list that have the given option type.
	 * 
	 * @param from
	 * 		The list that contains options.
	 * @param to
	 * 		The list that will contain all options of the from list with the corresponding type.
	 * @param typeName
	 * 		The name of the option type which the options have to have.
	 */
	private void sameTypeFilter(ArrayList<String> from, ArrayList<String> to, String typeName) {
		ArrayList<String> temp = new ArrayList<String>() ;
		for(String i: from){
			if(allOptions.get(i).getType().equals(typeName)) {
				to.add(i);
				temp.add(i);
			}
		}
		for(String i: temp){
			from.remove(i);
		}
	}

	/**
	 * Removes all strings that reside in a list, from another list.
	 * 
	 * @param strings
	 * 		The list in which strings will be removed.
	 * @param stringsToBeRemoved
	 * 		The list of strings that have to be removed.
	 * @throws VehicleCatalogException
	 * 		If a string in stringsToBeRemoved is not in strings.
	 */
	private void checkAndRemove(ArrayList<String> strings, ArrayList<String> stringsToBeRemoved) throws VehicleCatalogException {
		if(strings == stringsToBeRemoved) return;
		for(String i: stringsToBeRemoved){
			if( !strings.remove(i)) 
				throw new VehicleCatalogException("Option does not exists: "+ i);
		}
	}

	/**
	 * Creates an option.
	 * 
	 * @param description
	 * 		The description of the option to be made.
	 * @param typeName
	 * 		The name of the type of the option to be made.
	 * @param incompatibles
	 * 		The list containing descriptions of options incompatible with the option to be made.
	 * @return the new option
	 * @throws VehicleCatalogException
	 * 		If the option type is not supported.
	 */
	private Option createOption(String description, String typeName, ArrayList<String> incompatibles) throws VehicleCatalogException {
		
		OptionType type = (OptionType) VehicleCatalog.taskTypeCreator.getTaskType(typeName);
		Option result = new Option(description, type);
		for(Option option : collectOption(incompatibles)){
			result.addIncompatible(option);
			option.addIncompatible(result);
		}
		return result;
		

	}
	
	
	/**
	 * Creates an option.
	 * 
	 * @param description
	 * 		The description of the part to be made.
	 * @param typeName
	 * 		The name of the type of the part to be made.
	 * @return the new part
	 * @throws VehicleCatalogException
	 * 		If the part type is not supported.
	 */
	private Part createPart(String description, String typeName) throws VehicleCatalogException {
		try{
			PartType type = (PartType) VehicleCatalog.taskTypeCreator.getTaskType(typeName);
			if(type == null){
				System.out.println("damn");
			}
			Part result = new Part(description,  type);
			return result;
		}catch(IllegalArgumentException e){
			throw new VehicleCatalogException("no valid type: " + typeName);
		}

	}
	
	
	/**
	 * Makes a list of options made from a list of option descriptions.
	 * 
	 * @param descriptions
	 * 		The list of descriptions of the options to be made.
	 * @return a list of options corresponding to the given option descriptions
	 * @throws VehicleCatalogException 
	 * 		If the option does not exist.
	 */
	private ArrayList<Option> collectOption(ArrayList<String> descriptions) throws VehicleCatalogException {
		ArrayList<Option> options = new ArrayList<Option>();
		for(String description : descriptions){
			Option option = allOptions.get(description);
			if(option != null)
				options.add(option);
			else {
				throw new VehicleCatalogException("Option does not exists: "+ description);
			}
		}
		return options;
	}
}

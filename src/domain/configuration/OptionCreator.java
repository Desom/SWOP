package domain.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class OptionCreator {

	private HashMap<String,Option> allOptions;
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

	/**
	 * Creates the options from the file.
	 * 
	 * @return all options contained in the file.
	 * @throws IOException
	 * @throws CarModelCatalogException
	 * 		If an option line is not in the right format.
	 */
	public ArrayList<Option> createOptions() throws IOException, CarModelCatalogException{
		BufferedReader input = new BufferedReader(new FileReader(optionpath));
		this.allOptions = new HashMap<String,Option>();
		String inputline = input.readLine();
		while( inputline!=null){
			processOptionLine(inputline);
			inputline = input.readLine();
		}
		input.close();
		input = new BufferedReader(new FileReader(dependancypath));
		while( inputline!=null){
			processDependancyLine(inputline);
			inputline = input.readLine();
		}
		input.close();
		return new ArrayList<Option>(allOptions.values());
	}

	private void processDependancyLine(String inputline) throws CarModelCatalogException {
		String[] input=inputline.split(";");
		if(input.length != 2) throw new CarModelCatalogException("Dependancy: wrong input format: " + inputline);
		if(!allOptions.containsKey(input[0])) throw new CarModelCatalogException("Option does not exist: " + input[0]);
		ArrayList<String> optionNames = new ArrayList<String>();
		for(String j:input[1].split(","))optionNames.add(j);
		allOptions.get(input[0]).addDependancy(collectOption(optionNames));
	}
	/**
	 * Processes a line which stores the information of a single option.
	 * @param inputline
	 * 		The line to be processed.
	 * @throws CarModelCatalogException 
	 * 		If the option the line describes already exists
	 * 		If the line isn't in the right format
	 * 		If the option the line describes is of the wrong subtype
	 * 		IF the lines use an option that does not exist
	 */
	private void processOptionLine(String inputline) throws CarModelCatalogException   {
		String[] input=inputline.split(";");
		if(input.length != 3) throw new CarModelCatalogException("Option: wrong input format: " + inputline);
		if(allOptions.containsKey(input[0])) throw new CarModelCatalogException("Option already exists: " + input[0]);
		ArrayList<String> incomp = new ArrayList<String>(Arrays.asList(input[2].split(",")));
		ArrayList<String> comp = new ArrayList<String>();
		comp.addAll(allOptions.keySet());
		checkAndRemove(comp,incomp);
		sameTypeFilter(comp,incomp,input[1]);
		allOptions.put(input[0], createOption(input[0],input[1], incomp));
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
	 * 
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
	 * @throws CarModelCatalogException
	 * 		If a string in stringsToBeRemoved is not in strings.
	 */
	private void checkAndRemove(ArrayList<String> strings, ArrayList<String> stringsToBeRemoved) throws CarModelCatalogException {
		if(strings == stringsToBeRemoved) return;
		for(String i: stringsToBeRemoved){
			if( !strings.remove(i)) throw new CarModelCatalogException("Option does not exists: "+ i);
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
	 * @throws CarModelCatalogException
	 * 		If the option type is not supported.
	 */
	private Option createOption(String description, String typeName, ArrayList<String> incompatibles) throws CarModelCatalogException {
		try{
			Option result = new Option(description,  OptionType.valueOf(typeName));
			for(Option option : collectOption(incompatibles)){
				result.addIncompatible(option);
				option.addIncompatible(result);
			}
			return result;
		}catch(IllegalArgumentException e){
			throw new CarModelCatalogException("no valid type: " + typeName);
		}

	}
	/**
	 * Makes a list of options made from a list of option descriptions.
	 * 
	 * @param descriptions
	 * 		The list of descriptions of the options to be made.
	 * @return a list of options corresponding to the given option descriptions
	 * @throws CarModelCatalogException 
	 * 		If the option does not exist.
	 */
	private ArrayList<Option> collectOption(ArrayList<String> descriptions) throws CarModelCatalogException {
		ArrayList<Option> options = new ArrayList<Option>();
		for(String description : descriptions){
			Option option = allOptions.get(description);
			if(option != null)
				options.add(option);
			else {
				throw new CarModelCatalogException("Option does not exists: "+ description);
			}
		}
		return options;
	}
}

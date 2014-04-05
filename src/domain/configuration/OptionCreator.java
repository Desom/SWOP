package domain.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OptionCreator {
	
	private HashMap<String,Option> allOptions;
	String path;
	
	public OptionCreator(){
		this("data/options.txt");
	}
	public OptionCreator(String path){
		this.path = path;
	}
	/***
	 * Create the options from a file
	 * @param path of the file 
	 * @return 
	 * @throws IOException Problems with accessing file
	 * @throws CarModelCatalogException an optionline is not in the right format
	 */
	public ArrayList<Option> createOptions() throws IOException, CarModelCatalogException{
		BufferedReader input = new BufferedReader(new FileReader(path));
		this.allOptions = new HashMap<String,Option>();
		String inputline = input.readLine();
		while( inputline!=null){
			processOptionLine(inputline);
			inputline = input.readLine();
		}
		input.close();
		return new ArrayList<Option>(allOptions.values());
	}
	/**
	 * proccessing a line which stores the information of a single option
	 * @param inputline the line to be processed
	 * @throws CarModelCatalogException The option the line describes alreasy exist , the line isn't in the right format,
	 * 									the option the line describes is of the wrong subtype or the lines use an option that does not exist
	 */
	private void processOptionLine(String inputline) throws CarModelCatalogException   {
		String[] input=inputline.split(";");
		if(input.length != 3) throw new CarModelCatalogException("Option: wrong input format: " + inputline);
		if(allOptions.containsKey(input[0])) throw new CarModelCatalogException("Option already exists: " + input[0]);
		ArrayList<String> incomp = new ArrayList<String>();
		addAll(incomp , input[2].split(","));
		ArrayList<String> comp = new ArrayList<String>();
		comp.addAll(allOptions.keySet());
		checkAndRemove(comp,incomp);
		sameTypeFilter(comp,incomp,input[1]);
		allOptions.put(input[0], createOption(input[0],input[1], incomp));
	}
	private void addAll(ArrayList<String> incomp, String[] split) {
		for(String i: split) incomp.add(i);
	
	}
	/**
	 * this moves all option of a specific type that reside in one list to another list
	 * @param comp the list that contains options may move from
	 * @param incomp the list where may move to
	 * @param string the specified type
	 * 
	 */
	private void sameTypeFilter(ArrayList<String> comp, ArrayList<String> incomp,
			String string) {
		ArrayList<String> temp = new ArrayList<String>() ;
		for(String i: comp){
			if(allOptions.get(i).getType().toString().equals(string)) {
				incomp.add(i);
				temp.add(i);
			}
		}
		for(String i: temp){
			comp.remove(i);
		}
	}
	/**
	 * this removes all strings that reside in a certain list, from another list
	 * @param comp the list where string may be removed from
	 * @param incomp the list which strings will be removed
	 * @throws CarModelCatalogException a string in incomp is not in comp
	 */
	private void checkAndRemove(ArrayList<String> comp, ArrayList<String> incomp) throws CarModelCatalogException {
		if(comp == incomp) return;
		for(String i: incomp){
			if( !comp.remove(i)) throw new CarModelCatalogException("Option does not exists: "+ i);
		}
	}
	/**
	 * creates an option
	 * @param description the description of the made option
	 * @param type the type of the made option
	 * @param comp the list containing the descriptions compatible with the made option
	 * @param incomp the list containing the descriptions incompatible with the made option
	 * @return the made option
	 * @throws CarModelCatalogException The option type is not supported
	 */
	private Option createOption(String description, String type, ArrayList<String> incomp) throws CarModelCatalogException {
		try{
		Option result=  new Option(description,  OptionType.valueOf(type));
		for(Option opt :collectOption(incomp)){
			result.setIncompatible(opt);
			opt.setIncompatible(result);
		}
		return result;
		}catch(IllegalArgumentException e){
		throw new CarModelCatalogException("no valid type: " + type);
		}
		
	}
	/**
	 *  makes a list of option made of a list of descriptions
	 * @param comp the list of descriptions of options
	 * @return a list of options which corresponds with comp
	 * @throws CarModelCatalogException 
	 */
	private ArrayList<Option> collectOption(ArrayList<String> comp) throws CarModelCatalogException {
		ArrayList<Option> result = new ArrayList<Option>();
		for(String i: comp){
			Option e = allOptions.get(i);
			if(e !=null)result.add(e);
			else {
				throw new CarModelCatalogException("Option does not exists: "+ i);
			}
		}
		return result;
	}
	
	
}

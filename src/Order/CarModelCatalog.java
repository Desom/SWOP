package Order;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Car.CarModel;
import Car.Option;
import Order.OptionSubTypes.*;
import User.User;
import User.UserAccessException;



public class CarModelCatalog {
	private HashMap<String,Option> All_Options;
	private HashMap<String,CarModel> All_CarModels;
	private ArrayList<String> All_Optiontypes;
	/**
	 * Create a carmodelcatalog
	 * @param Optionfile this file is the file which contains the data of options
	 * @param Modelfile this file is the file which contains the data of models
	 * @throws IOException 
	 * @throws CarModelCatalogException
	 */
	public CarModelCatalog(String Optionfile, String Modelfile) throws IOException, CarModelCatalogException{
		All_Options = new HashMap<String,Option>();
		All_CarModels= new HashMap<String,CarModel>();
		All_Optiontypes = new ArrayList<String>();
		All_Optiontypes.add("Color");
		All_Optiontypes.add("Body");
		All_Optiontypes.add("Engine");
		All_Optiontypes.add("Gearbox");
		All_Optiontypes.add("Airco");
		All_Optiontypes.add("Wheels");
		All_Optiontypes.add("Seats");
		createOptions(Optionfile);
		createModels(Modelfile);
		
	}
	public CarModelCatalog() throws IOException, CarModelCatalogException{
		this("options.txt", "models.txt");
	}
	/***
	 * Create the options from a file
	 * @param path of the file 
	 * @throws IOException Problems with accessing file
	 * @throws CarModelCatalogException an optionline is not in the right format
	 */
	private void createOptions(String path) throws IOException, CarModelCatalogException{
		BufferedReader input = new BufferedReader(new FileReader(path));
		String inputline = input.readLine();
		while( inputline!=null){
			processOptionLine(inputline);
			inputline = input.readLine();
		}
		input.close();
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
		if(All_Options.containsKey(input[0])) throw new CarModelCatalogException("Option already exists: " + input[0]);
		ArrayList<String> incomp = new ArrayList<String>();
		addall(incomp , input[2].split(","));
		ArrayList<String> comp = new ArrayList<String>();
		comp.addAll(All_Options.keySet());
		check_and_remove(comp,incomp);
		sametypefilter(comp,incomp,input[1]);
		All_Options.put(input[0], create_Option(input[0],input[1], comp,incomp));
	}

	private void addall(ArrayList<String> incomp, String[] split) {
		for(String i: split) incomp.add(i);

	}
	/**
	 * this moves all option of a specific type that reside in one list to another list
	 * @param comp the list that contains options may move from
	 * @param incomp the list where may move to
	 * @param string the specified type
	 * 
	 */
	private void sametypefilter(ArrayList<String> comp, ArrayList<String> incomp,
			String string) {
		ArrayList<String> temp = new ArrayList<String>() ;
		for(String i: comp){
			if(All_Options.get(i).getType().equals(string)) {
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
	private void check_and_remove(ArrayList<String> comp, ArrayList<String> incomp) throws CarModelCatalogException {
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
	private Option create_Option(String description, String type, ArrayList<String> comp, ArrayList<String> incomp) throws CarModelCatalogException {
		if(type.equals("Color"))return new Color(description, CollectOption(comp), CollectOption(incomp));
		if(type.equals("Body"))return new Body(description, CollectOption(comp), CollectOption(incomp));
		if(type.equals("Engine"))return new Engine(description, CollectOption(comp), CollectOption(incomp));
		if(type.equals("Gearbox"))return new Gearbox(description, CollectOption(comp), CollectOption(incomp));
		if(type.equals("Airco"))return new Airco(description, CollectOption(comp), CollectOption(incomp));
		if(type.equals("Wheels"))return new Wheels(description, CollectOption(comp), CollectOption(incomp));
		if(type.equals("Seats"))return new Seats(description, CollectOption(comp), CollectOption(incomp));
		throw new CarModelCatalogException("no valid type: " + type);
	}
	/**
	 *  makes a list of option made of a list of descriptions
	 * @param comp the list of descriptions of options
	 * @return a list of options which corresponds with comp
	 * @throws CarModelCatalogException 
	 */
	private ArrayList<Option> CollectOption(ArrayList<String> comp) throws CarModelCatalogException {
		ArrayList<Option> result = new ArrayList<Option>();
		for(String i: comp){
			Option e = All_Options.get(i);
			if(e !=null)result.add(e);
			else {
				throw new CarModelCatalogException("Option does not exists: "+ i);
			}
		}
		return result;
	}
	/**
	 * Create the models from a file
	 * @param path of the file 
	 * @throws IOException Problems with accessing file
	 * @throws CarModelCatalogException an modelline is not in the right format
	 */
	private void createModels(String path) throws IOException, CarModelCatalogException{
		BufferedReader input = new BufferedReader(new FileReader(path));
		String inputline = input.readLine();
		while( inputline!=null){
			processModelLine(inputline);
			inputline = input.readLine();
		}
		input.close();
	}
	/**
	 * 
	 * @param inputline
	 * @throws CarModelCatalogException
	 */
	private void processModelLine(String inputline) throws CarModelCatalogException {
		String[] input=inputline.split(";");
		if(input.length != 2) throw new CarModelCatalogException("Model: wrong input format: " + inputline);
		if(All_CarModels.containsKey(input[0])) throw new CarModelCatalogException("Model name already exists: "+input[0] );
		try{
			ArrayList<String> a = new ArrayList<String>();
			addall(a,input[1].split(","));
			All_CarModels.put(input[0], new CarModel(input[0], CollectOption(a), this.All_Optiontypes));
		}catch(ClassCastException e){
			throw new CarModelCatalogException("Wrong Option Type in form: " + inputline);
		}
	}

	@SuppressWarnings("unchecked")
	public List<CarModel> getAllModels(User user) throws UserAccessException {
		if(user.canPerform("getAllModels"))	return  new ArrayList<CarModel>(((HashMap<String,CarModel>)All_CarModels.clone()).values());
		else throw new UserAccessException(user, "getAllModels");
	}

	public CarModel getCarModel(String name){
		return this.All_CarModels.get(name);

	}

	public Option getOption(String description){
		return this.All_Options.get(description);

	}
	@SuppressWarnings("unchecked")
	public ArrayList<String> getAllOptionTypes(){
		return (ArrayList<String>) All_Optiontypes.clone();
	}
	public ArrayList<String> getAllModelnames(User user) throws UserAccessException {
		ArrayList<String> modelnamen = new ArrayList<String>();
		for(CarModel i:this.getAllModels(user)){
				modelnamen.add(i.getName());
		}
		return modelnamen;
	}
	
}

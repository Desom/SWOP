package Main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import OptionSubTypes.*;



public class CarModelCatalog {
	private HashMap<String,Option> All_Options;
	private HashMap<String,CarModel> All_CarModels;
	
	public CarModelCatalog(String Optionfile, String Modelfile) throws IOException, CarModelCatalogException{
		All_Options = new HashMap<String,Option>();
		All_CarModels= new HashMap<String,CarModel>();
		createOptions(Optionfile);
		createModels(Modelfile);
	}
	public CarModelCatalog() throws IOException, CarModelCatalogException{
		this("options.txt", "models.txt");
	}

	private void createOptions(String path) throws IOException, CarModelCatalogException{
		BufferedReader input = new BufferedReader(new FileReader(path));
		String inputline = input.readLine();
		while( inputline!=null){
			processOptionLine(inputline);
			inputline = input.readLine();
		}
		input.close();
	}
	
	private void processOptionLine(String inputline) throws CarModelCatalogException {
		String[] input=inputline.split(";");
		if(input.length != 3) throw new CarModelCatalogException("Option: wrong input format: " + inputline);
		if(All_Options.containsKey(input[0])) throw new CarModelCatalogException("Option already exists: " + input[0]);
		String[] incomp = input[2].split(",");
		ArrayList<String> comp = new ArrayList<String>();
		comp.addAll(All_Options.keySet());
		check_and_remove(comp,incomp);
		String[] f = new String[comp.size()];
		All_Options.put(input[0], create_Option(input[0],input[1], comp.toArray(f),incomp));
	}

	private void check_and_remove(ArrayList<String> comp, String[] incomp) throws CarModelCatalogException {
		for(String i: incomp) if( !comp.remove(i)) throw new CarModelCatalogException("Option does not exists: "+ i);
	}
	
	private Option create_Option(String description, String type, String[] comp, String[] incomp) throws CarModelCatalogException {
		if(type.equals("Color"))return new Color(description, CollectOption(comp), CollectOption(incomp));
		if(type.equals("Body"))return new Body(description, CollectOption(comp), CollectOption(incomp));
		if(type.equals("Engine"))return new Engine(description, CollectOption(comp), CollectOption(incomp));
		if(type.equals("Gearbox"))return new Gearbox(description, CollectOption(comp), CollectOption(incomp));
		if(type.equals("Airco"))return new Airco(description, CollectOption(comp), CollectOption(incomp));
		if(type.equals("Wheels"))return new Wheels(description, CollectOption(comp), CollectOption(incomp));
		if(type.equals("Seats"))return new Seats(description, CollectOption(comp), CollectOption(incomp));
		throw new CarModelCatalogException("no valid type: " + type);
	}

	private ArrayList<Option> CollectOption(String[] set) {
		ArrayList<Option> result = new ArrayList<Option>();
		for(String i: set) result.add(All_Options.get(i));
		return result;
	}

	private void createModels(String path) throws IOException, CarModelCatalogException{
		BufferedReader input = new BufferedReader(new FileReader(path));
		String inputline = input.readLine();
		while( inputline!=null){
			processModelLine(inputline);
			inputline = input.readLine();
		}
		input.close();
	}

	private void processModelLine(String inputline) throws CarModelCatalogException {
		String[] input=inputline.split(";");
		if(input.length != 9) throw new CarModelCatalogException("Model: wrong input format: " + inputline);
		if(All_CarModels.containsKey(input[0])) throw new CarModelCatalogException("Model name already exists: "+input[0] );
		for(int i=1; i<8; i++) if(!All_Options.containsKey(input[i])) throw new CarModelCatalogException("Option does not exists: "+ input[i]);
		try{
			All_CarModels.put(input[0], new CarModel(input[0], CollectOption(input[7].split(",")) ,(Airco) All_Options.get(input[1]), (Body) All_Options.get(input[2]), (Color) All_Options.get(input[3]), (Engine) All_Options.get(input[4]), (Gearbox) All_Options.get(input[5]), (Seats) All_Options.get(input[6]), (Wheels) All_Options.get(input[7])));
		}catch(ClassCastException e){
			throw new CarModelCatalogException("Wrong Option Type in form: " + inputline);
		}
	}

	@SuppressWarnings("unchecked")
	public CarModel[] getAllModels(User user) throws CarModelCatalogException {
		if(user.canPerform("getAllModels"))	return (CarModel[]) ((HashMap<String,CarModel>)All_CarModels.clone()).values().toArray();
		else throw new CarModelCatalogException("user has no rights for getAllModels");
	}
	
	public CarModel getCarModel(String name){
		return this.All_CarModels.get(name);
		
	}
	
	public Option getOption(String description){
		return this.All_Options.get(description);
		
	}
}

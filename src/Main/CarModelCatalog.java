package Main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import OptionSubTypes.*;



public class CarModelCatalog {
	private HashMap<String,Option> All_Options;
	private HashMap<String,CarModel> All_CarModels;
	private LinkedList<String> All_Optiontypes;
	public CarModelCatalog(String Optionfile, String Modelfile) throws IOException, CarModelCatalogException{
		All_Options = new HashMap<String,Option>();
		All_CarModels= new HashMap<String,CarModel>();
		createOptions(Optionfile);
		createModels(Modelfile);
		All_Optiontypes = new LinkedList<String>();
		All_Optiontypes.add("Color");
		All_Optiontypes.add("Body");
		All_Optiontypes.add("Engine");
		All_Optiontypes.add("Gearbox");
		All_Optiontypes.add("Airco");
		All_Optiontypes.add("Wheels");
		All_Optiontypes.add("Seats");
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
	private void check_and_remove(ArrayList<String> comp, ArrayList<String> incomp) throws CarModelCatalogException {
		for(String i: incomp) if( !comp.remove(i)) throw new CarModelCatalogException("Option does not exists: "+ i);
	}

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

	private ArrayList<Option> CollectOption(ArrayList<String> comp) {
		ArrayList<Option> result = new ArrayList<Option>();
		for(String i: comp) result.add(All_Options.get(i));
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
			ArrayList<String> a = new ArrayList<String>();
			addall(a,input[7].split(","));
			All_CarModels.put(input[0], new CarModel(input[0], CollectOption(a) ,(Airco) All_Options.get(input[1]), (Body) All_Options.get(input[2]), (Color) All_Options.get(input[3]), (Engine) All_Options.get(input[4]), (Gearbox) All_Options.get(input[5]), (Seats) All_Options.get(input[6]), (Wheels) All_Options.get(input[7])));
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
	public LinkedList<String> getAllOptionTypes(){
		return (LinkedList<String>) All_Optiontypes.clone();
	}
	public ArrayList<String> getAllModelnames(User user) throws UserAccessException {
		ArrayList<String> modelnamen = new ArrayList<String>();
		for(CarModel i:this.getAllModels(user)){
				modelnamen.add(i.getName());
		}
		return modelnamen;
	}
	public ArrayList<String> filterOptiontype(String type,ArrayList<Option> chosenoptions, CarModel model) {
		ArrayList<String> result = new ArrayList<String>();
		for(Option i: model.getOptions()){
			if(i.getType().equals(type)){
				Boolean incompatible = false;
				for(Option j: chosenoptions){
					incompatible=	incompatible || j.conflictsWith(i);
				}
				if(!incompatible) result.add(i.getdescription());
			}
		}
		return result;
	}
}

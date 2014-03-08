package Main;

import java.util.ArrayList;
import java.util.HashMap;

public class Configuration {
	
	private final CarModel model;
	private final HashMap<String,Option> options;
	
	public Configuration(CarModel model, ArrayList<Option> options) throws Exception{
		this.model = model;
		this.options = new HashMap<String,Option>();
		addall(this.options, options);
		addmissingoptions();
		
	}
	
	private void addmissingoptions() {
		if(!options.containsKey("Color"))options.put(model.getDefault_Color().getType(),model.getDefault_Color());
		if(!options.containsKey("Body"))options.put(model.getDefault_Body().getType(),model.getDefault_Body());
		if(!options.containsKey("Engine"))options.put(model.getDefault_Engine().getType(),model.getDefault_Engine());
		if(!options.containsKey("Gearbox"))options.put(model.getDefault_Gearbox().getType(),model.getDefault_Gearbox());
		if(!options.containsKey("Airco"))options.put(model.getDefault_Airco().getType(),model.getDefault_Airco());
		if(!options.containsKey("Wheels"))options.put(model.getDefault_Wheels().getType(),model.getDefault_Wheels());
		if(!options.containsKey("Seats"))options.put(model.getDefault_Seats().getType(),model.getDefault_Seats());
		
	}

	private void addall(HashMap<String, Option> optionmap,
			ArrayList<Option> optionlist) throws Exception  {
		for(int i=0; i< optionlist.size();i++){
			for(int j= i+1; j<optionlist.size();j++){
				if(optionlist.get(i).conflictsWith(optionlist.get(j))){
					throw new Exception("error conflicting types");
				}
			}
			optionmap.put(optionlist.get(i).getType(), optionlist.get(i));
			}
		}

	protected Option getOption(String optionType){
		return options.get(optionType);
	}
	
	@SuppressWarnings("unchecked")
	protected ArrayList<Option> getAllOptions(){
		return new ArrayList<Option>(options.values());
	}
	
	public String toString(){
		String s = model.toString() + "\n Options: \n";
		for(Option o: options.values()){
			s += o.toString() + "\n";
		}
		return s;
	}

}

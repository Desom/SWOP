package Main;

import java.util.ArrayList;
import java.util.HashMap;

public class Configuration {
	
	private final CarModel model;
	private final HashMap<String,Option> options;
	
	/**
	 * Constructor of Configuration.
	 * 
	 * @param	model
	 * 			The car model used for this configuration
	 * @param	options
	 * 			The options used for this configuration
	 * @throws	Exception
	 * 			If the given options are conflicting with each other
	 */
	public Configuration(CarModel model, ArrayList<Option> options) {
		this.model = model;
		this.options = new HashMap<String,Option>();
		addall(options);
		addmissingoptions();
		
	}
	
	/**
	 * Fills the hashmap options with all options of this configuration and adds the default options for that car model
	 * 		if the option is missing.
	 */
	@SuppressWarnings("static-access")
	private void addmissingoptions() {
		if(!options.containsKey("Color"))options.put(model.getDefault_Color().getType(),model.getDefault_Color());
		if(!options.containsKey("Body"))options.put(model.getDefault_Body().getType(),model.getDefault_Body());
		if(!options.containsKey("Engine"))options.put(model.getDefault_Engine().getType(),model.getDefault_Engine());
		if(!options.containsKey("Gearbox"))options.put(model.getDefault_Gearbox().getType(),model.getDefault_Gearbox());
		if(!options.containsKey("Airco"))options.put(model.getDefault_Airco().getType(),model.getDefault_Airco());
		if(!options.containsKey("Wheels"))options.put(model.getDefault_Wheels().getType(),model.getDefault_Wheels());
		if(!options.containsKey("Seats"))options.put(model.getDefault_Seats().getType(),model.getDefault_Seats());
		
	}

	/**
	 * Adds all options to this configuration and checks for conflicting options.
	 * 
	 * @param	optionlist
	 * 			The list of all options to be added to this configuration
	 * @throws	Exception
	 * 			If there are conflicting options to be added
	 */
	@SuppressWarnings("static-access")
	private void addall(ArrayList<Option> optionlist) throws IllegalArgumentException {
		for(int i=0; i< optionlist.size();i++){
			for(int j= i+1; j<optionlist.size();j++){
				if(optionlist.get(i).conflictsWith(optionlist.get(j))){
					throw new IllegalArgumentException("There are conflicting options");
				}
			}
			options.put(optionlist.get(i).getType(), optionlist.get(i));
			}
		}

	/**
	 * Gives the option corresponding to the option type.
	 * 
	 * @param	optionType
	 * 			The string that specifies the option type
	 * @return	the options that corresponds to the option type
	 */
	protected Option getOption(String optionType){
		return options.get(optionType);
	}
	
	/**
	 * Returns all options of this configuration.
	 * 
	 * @return	the list of all options of the configuration
	 */
	protected ArrayList<Option> getAllOptions(){
		return new ArrayList<Option>(options.values());
	}
	
	/**
	 * Returns the string that represents this configuration.
	 */
	public String toString(){
		String s = model.toString() + "\n Options: \n";
		for(Option o: options.values()){
			s += o.toString() + "\n";
		}
		return s;
	}

}

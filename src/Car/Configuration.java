package Car;

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
		possible(model, options);
		this.model = model;
		this.options = new HashMap<String,Option>();
		addall(options);
		searchmissingoptions();
		
	}
	
	private void possible(CarModel model, ArrayList<Option> options) {
		for(Option i: options){
			if(!model.getOptions().contains(i)){
				throw new IllegalArgumentException("dit model bevat deze optie niet");
			}
		}
	}

	/**
	 * Fills the hashmap options with all options of this configuration and adds the default options for that car model
	 * 		if the option is missing.
	 */
	private void searchmissingoptions() {
		if(!options.containsKey("Color"))throw new IllegalArgumentException();
		if(!options.containsKey("Body"))throw new IllegalArgumentException();
		if(!options.containsKey("Engine"))throw new IllegalArgumentException();
		if(!options.containsKey("Gearbox"))throw new IllegalArgumentException();
		if(!options.containsKey("Airco"))throw new IllegalArgumentException();
		if(!options.containsKey("Wheels"))throw new IllegalArgumentException();
		if(!options.containsKey("Seats")) throw new IllegalArgumentException();
		
	}

	/**
	 * Adds all options to this configuration and checks for conflicting options.
	 * 
	 * @param	optionlist
	 * 			The list of all options to be added to this configuration
	 * @throws	Exception
	 * 			If there are conflicting options to be added
	 */
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
	/**
	 * returns the option of a specific type
	 * @param type
	 * @return the option of a specific type
	 * 			null if no option is found of that type
	 */
	public Option getOptionOfType(String type){
		return this.options.get(type);
	}

	/**
	 * Returns the CarModel from this configuration 
	 * 
	 * @return The CarModel of this configuration
	 */
	public CarModel getModel() {
		return model;
	}
}

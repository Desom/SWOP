package domain.configuration;

import java.util.ArrayList;
import java.util.HashMap;

public class Configuration {
	
	private final CarModel model;
	private final HashMap<OptionType,Option> options;
	
	
	/**
	 * Constructor of Configuration.
	 * 
	 * @param	model
	 * 			The car model used for this configuration
	 * @param	options
	 * 			The options used for this configuration
	 * @throws	IllegalArgumentException
	 * 			If the given options are conflicting with each other
	 */
	public Configuration(CarModel model, ArrayList<Option> options) throws IllegalArgumentException {
		possible(model, options);
		this.model = model;
		this.options = new HashMap<OptionType,Option>();
		addall(options);
		searchmissingoptions();
		
	}
	
	/**
	 * Get the expected time that will be spent working on completing all options taking into account the model of this configuration 
	 * 
	 * @return The amount of options multiplied with the expected time it takes to complete an option for the current model.
	 * 			If no model is specified, the default time per option is 60 minutes.
	 */
	public int getExpectedWorkingTime(){
		if(this.model == null){
			return 60*this.options.size();
		}
		return this.model.getExpectedTaskTime()*this.options.size();
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
		for(OptionType i : OptionType.values()){
			if(!options.containsKey(i))throw new IllegalArgumentException();
		}	
	}

	/**
	 * Adds all options to this configuration and checks for conflicting options.
	 * 
	 * @param	optionlist
	 * 			The list of all options to be added to this configuration
	 * @throws	IllegalArgumentException
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
	 * Returns all options of this configuration.
	 * 
	 * @return	the list of all options of the configuration
	 */
	//TODO echte clonen van option objecten?
	public ArrayList<Option> getAllOptions(){
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
	 * Gives the option corresponding to the option type.
	 * 
	 * @param	optionType
	 * 			The string that specifies the option type
	 * @return	the option that corresponds to the option type
	 */
	public Option getOptionOfType(String optionType){
		return this.options.get(optionType);
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

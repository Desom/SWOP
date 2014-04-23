package domain.configuration;
import java.util.ArrayList;


public class CarModel {
	
	private final String name;
	private final ArrayList<Option> possibleOptions;
	private final int expectedTaskTime;
	
	/**
	 * Constructor of CarModel.
	 * 
	 * @param name
	 * 		Name of the car model.
	 * @param possibleOptions
	 * 		A list of options which are possible for this car model.
	 * @param expectedTaskTime
	 * 		The time it normally takes to complete a task of this car model.
	 * @throws CarModelCatalogException
	 * 		If the name or possibleOptions equals null or if an option type is not represented in the possibleOptions.
	 */
	public CarModel(String name, ArrayList<Option> possibleOptions, int expectedTaskTime) throws CarModelCatalogException{
		if(name == null || possibleOptions == null)
			throw new CarModelCatalogException("null in non null value of Model");

		for(OptionType type:OptionType.values()){
			if(type.isMandatory() && !existstype(type, possibleOptions))
				throw new CarModelCatalogException("Missing type: "+ type);
		}
		this.possibleOptions = possibleOptions;
		this.name = name;
		this.expectedTaskTime = expectedTaskTime;
	}
	
	/**
	 * Constructor of CarModel.
	 * Creates a car model with a default expected working time of 60 minutes.
	 * 
	 * @param name
	 * 		Name of the car model.
	 * @param possibleOptions
	 * 		A list of options which are possible for this car model.
	 * @throws CarModelCatalogException
	 * 		If the name or possibleOptions equals null or if an option type is not represented in the possibleOptions.
	 */
	public CarModel(String name, ArrayList<Option> possibleOptions) throws CarModelCatalogException{
		this(name, possibleOptions, 60);
	}
	
	/**
	 * Checks if a the given option type is represented in the given list of options.
	 * @param type
	 * 		The type which has to represented.
	 * @param options
	 * 		The options which have to represent the option type.
	 * @return true if an option with the type is found, otherwise false
	 */
	private boolean existstype(OptionType type, ArrayList<Option> options) {
		for(Option option : options)
			if (option.getType().equals(type))
				return true;
		return false;
	}

	/**
	 * Returns a list of possible options for this car model.
	 * 
	 * @return a list of possible options for this car model
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Option> getPossibleOptions() {
		return (ArrayList<Option>) possibleOptions.clone();
	}

	/**
	 * Returns the name of this car model.
	 * 
	 * @return the name of this car model
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns all possible options of a specific type.
	 * 
	 * @param type
	 * 		The type of which all possible options have to be returned.
	 * @return all possible options of the given type
	 */
	public ArrayList<Option> getOfOptionType(OptionType type) {
		ArrayList<Option> optionsOfType = new ArrayList<Option>();
		for(Option option : possibleOptions){
			if(option.getType().equals(type))
				optionsOfType.add(option);
		}
		return optionsOfType;
	}
	
	/**
	 * Get the expected working time spent on a task for this model.
	 * 
	 * @return returns the expected time it takes to complete a task
	 */
	public int getExpectedTaskTime(){
		return expectedTaskTime;
	}

	@Override
	public String toString(){
		return "Model : " + name;
	}
}

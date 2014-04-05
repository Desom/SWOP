package domain.configuration;
import java.util.ArrayList;


public class CarModel {
	private final String name;
	private final ArrayList<Option> possibleOptions;
	/**
	 * create a car model
	 * @param name name of the model
	 * @param optionList the options which are possible in this model
	 * @param optiontypes all possible types of options that are required
	 * @throws CarModelCatalogException a paramater is null or a type of option is not represented
	 */
	public CarModel(String name,ArrayList<Option> optionList) throws CarModelCatalogException{
		if(name == null || optionList == null) throw new CarModelCatalogException("null in non null value of Model");

		for(OptionType type:OptionType.values()){
			if(!existstype(type,optionList)) throw new CarModelCatalogException("Missing type: "+ type);
		}
		possibleOptions = optionList;
		this.name=name;

	}
	/**
	 * looks if a type of options is represented in a list
	 * @param type
	 * @param optionList
	 * @return true if an option with the type is found
	 * 			false if an option with the type is not found
	 */
	private boolean existstype(OptionType type, ArrayList<Option> optionList) {
		boolean result = false;
		for(Option option: optionList ){
			result= result || option.getType().equals(type);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Option> getOptions() {
		return (ArrayList<Option>) possibleOptions.clone();
	}


	public String getName() {
		return name;
	}

	public String toString(){
		return "Model : " + name;
	}
	/**
	 * Get all options of a specific type
	 * @param string the type
	 * @return
	 */
	public ArrayList<Option> getOfOptionType(OptionType string) {
		ArrayList<Option> result = new ArrayList<Option>();
		for(Option i: possibleOptions){
			if(i.getType().equals(string)) result.add(i);
		}
		return result;
	}

}

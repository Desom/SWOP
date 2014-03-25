package Car;
import java.util.ArrayList;

import Order.CarModelCatalogException;


public class CarModel {
	private final String Name;
	private final ArrayList<Option> PossibleOptions;
	/**
	 * create a car model
	 * @param Name name of the model
	 * @param OptionList the options which are possible in this model
	 * @param optiontypes all possible types of options that are required
	 * @throws CarModelCatalogException a paramater is null or a type of option is not represented
	 */
	public CarModel(String Name,ArrayList<Option> OptionList) throws CarModelCatalogException{
		if(Name == null || OptionList == null) throw new CarModelCatalogException("null in non null value of Model");

		for(OptionType type:OptionType.values()){
			if(!existstype(type,OptionList)) throw new CarModelCatalogException("Missing type: "+ type);
		}
		PossibleOptions = OptionList;
		this.Name=Name;

	}
	/**
	 * looks if a type of options is represented in a list
	 * @param type
	 * @param OptionList
	 * @return true if an option with the type is found
	 * 			false if an option with the type is not found
	 */
	private boolean existstype(OptionType type, ArrayList<Option> OptionList) {
		boolean result = false;
		for(Option option: OptionList ){
			result= result || option.getType().equals(type);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Option> getOptions() {
		return (ArrayList<Option>) PossibleOptions.clone();
	}


	public String getName() {
		return Name;
	}

	public String toString(){
		return "Model : " + Name;
	}
	/**
	 * Get all options of a specific type
	 * @param string the type
	 * @return
	 */
	public ArrayList<Option> getOfOptionType(OptionType string) {
		ArrayList<Option> result = new ArrayList<Option>();
		for(Option i: PossibleOptions){
			if(i.getType().equals(string)) result.add(i);
		}
		return result;
	}

}

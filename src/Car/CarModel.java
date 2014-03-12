package Car;
import java.util.ArrayList;

import Order.CarModelCatalogException;


public class CarModel {
	private final String Name;
	private final ArrayList<Option> PossibleOptions;

	public CarModel(String Name,ArrayList<Option> OptionList, ArrayList<String> optiontypes) throws CarModelCatalogException{
		if(Name == null || OptionList == null|| optiontypes == null) throw new CarModelCatalogException("null in non null value of Model");

		for(String type:optiontypes){
			if(!existstype(type,OptionList)) throw new CarModelCatalogException("Missing type");
		}
		PossibleOptions = OptionList;
		this.Name=Name;

	}

	private boolean existstype(String type, ArrayList<Option> OptionList) {
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

	public ArrayList<Option> getOfOptionType(String string) {
		ArrayList<Option> result = new ArrayList<Option>();
		for(Option i: PossibleOptions){
			if(i.getType().equals(string)) result.add(i);
		}
		return result;
	}

}

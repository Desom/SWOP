package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OurOrderform implements OrderForm{
	private HashMap<String,Option> options;
	private CarModel model;
	private User user;
	private CarModelCatalog catalog;
	public OurOrderform(User user, CarModelCatalog catalog) {
		options= new HashMap<String,Option>();
		this.user= user;
		this.catalog = catalog;
	}
	@Override
	public boolean SetModel(String model) {
		if(this.model == null){
			this.model = catalog.getCarModel(model);
			if(this.model != null)return true;
		}
		return false;
	}
	
	@Override
	public ArrayList<Option> getOptions() {
		return  new ArrayList<Option>(options.values());
	}

	@Override
	public CarModel getModel() {
		return model;
	}
	@Override
	public User getUser() {
		return user;
	}
	private boolean basicOptionRestrictions(){
		if(model==null)return false;
		return true;
		
	}

	@Override
	public List<String> getPossibleOptionsOfType(String type) {
		if(!this.basicOptionRestrictions()) return null;
		List<String> result = new ArrayList<String>();
			for(Option i: model.getOptions()){
				if(i.getType().equals(type)){
					Boolean incompatible = false;
					for(Option j: options.values()){
						incompatible=	incompatible || j.conflictsWith(i);
					}
					if(!incompatible) result.add(i.getdescription());
				}
			}
			return result;
	}


	@Override
	public boolean setOption(String description) {
		if(!this.basicOptionRestrictions()) return false;
		Option option = catalog.getOption(description);
		if(option == null) return false;
		if(model.getOptions().contains(option) && !this.options.containsKey(option.getType())){
			this.options.put(option.getType(), option);
			return true;
		}
		return false;
	}
	@Override
	public boolean CanPlaceType(String Type) {
		return options.containsKey(Type);
	}
	@Override
	public List<String> getAllModels(){
		List<String> result = new ArrayList<String>();
		try {
			for(CarModel i: catalog.getAllModels(user)){
				result.add(i.getName());
			}
		} catch (UserAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
	public List<String> getOptionTypes() {
				return catalog.getAllOptionTypes();
	}
}

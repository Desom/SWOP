package Main;

import java.util.ArrayList;

public class Configuration {
	
	private final CarModel model;
	private final ArrayList<Option> options;
	
	public Configuration(CarModel model, ArrayList<Option> options){
		this.model = model;
		this.options = options;
	}
	
	protected Option getOption(String optionType){
		
	}
	
	@SuppressWarnings("unchecked")
	protected ArrayList<Option> getAllOptions(){
		return (ArrayList<Option>) options.clone();
	}
	
	public String toString(){
		String s = model.toString() + "\n Options: \n";
		for(Option o: options){
			s += o.toString() + "\n";
		}
		return s;
	}

}

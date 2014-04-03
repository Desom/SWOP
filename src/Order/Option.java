package Order;
import java.util.ArrayList;

import Car.OptionType;


public class Option {
	private String description; 
	private ArrayList<Option> incompatibles;
	private OptionType type;
	/**
	 * creates an option
	 * @param description the description of the option
	 * @param compatibles the options this option is compatible is
	 * @param incompatibles the options this option is incompatible is
	 * @throws CarModelCatalogException a parameter is null or an option is both compatible and incompatible with  this option
	 */
	public Option(String description, OptionType type) throws CarModelCatalogException{
		if(description == null ||  type == null) throw new CarModelCatalogException("null in non null value of Option");
		this.incompatibles = new ArrayList<Option>();
		this.description =description;
		this.type = type;
		incompatibles.add(this);
	}

	public String getDescription(){
		return description;
	}
	void setIncompatible(Option option){
		if(!this.incompatibles.contains(option)){
			this.incompatibles.add(option);
		}
	}
	/**
	 * looks if an option can be in the same configuration as this option
	 * @param opt the other option
	 * @return true if the option can be in the same configuration as this option else no
	 */
	public Boolean conflictsWith(Option opt){
		if(this.incompatibles.contains(opt)) return true;
		return false;
	}
	public String toString(){
		return description;
	}

	public OptionType getType(){
		return type;
	}


}


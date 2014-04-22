package domain.configuration;
import java.util.ArrayList;


public class Option {
	private String description; 
	private ArrayList<Option> incompatibles;
	private OptionType type;
	private ArrayList<ArrayList<Option>> dependencies;// elke arrayList bevat een lijst van opties waarvan minstens 1 aanwezig moet zijn voor correctheid
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
		this.dependencies = new ArrayList<ArrayList<Option>>();
	}

	public String getDescription(){
		return description;
	}
	void setIncompatible(Option option){
		if(!this.incompatibles.contains(option)){
			this.incompatibles.add(option);
		}
	}
	void setDependancy(ArrayList<Option> dependency){
		this.dependencies.add(dependency);
	}
	/**
	 * looks if an option can be in the same configuration as this option
	 * @param opt the other option
	 * @return true if the option can be in the same configuration as this option else no
	 */
	public Boolean conflictsWith(Option opt){
		if(type == opt.getType()) return true;
		if(this.incompatibles.contains(opt)) return true;
		return false;
	}
	public String toString(){
		return description;
	}

	public OptionType getType(){
		return type;
	}
	
	/**
	 * Checks whether a list of options satisfies all dependencies of this option.
	 * 
	 * @param allOptions
	 * 		The list that has to be checked to satisfy all dependencies of this option.
	 */
	public boolean dependencyCheck(ArrayList<Option> allOptions){
		loop : for(ArrayList<Option> dependency : dependencies){
			for(Option option : dependency)
				if(allOptions.contains(option))
					continue loop;
			return false;
		}
		return true;
	}

}


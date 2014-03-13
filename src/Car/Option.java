package Car;
import java.util.ArrayList;

import Order.CarModelCatalogException;


public abstract class Option {
	 private String description; // 
	 private ArrayList<Option> compatibles;
	 private ArrayList<Option> incompatibles;
	 /**
	  * creates an option
	  * @param description the description of the option
	  * @param compatibles the options this option is compatible is
	  * @param incompatibles the options this option is incompatible is
	  * @throws CarModelCatalogException a parameter is null or an option is both compatible and incompatible with  this option
	  */
	public Option(String description, ArrayList<Option> compatibles, ArrayList<Option> incompatibles) throws CarModelCatalogException{
		 if(description == null || compatibles==null || incompatibles==null) throw new CarModelCatalogException("null in non null value of Option");
		 if( hascommonElement(compatibles,incompatibles)) throw new CarModelCatalogException("Option is both Compatible and incompatiblle with another option at the same type" );
		 this.compatibles =compatibles;
		 this.incompatibles =incompatibles;
		 this.description =description;
		 incompatibles.add(this);
	 }
	 /**
	  * check if two arrays have a common option
	  * @param compatibles
	  * @param incompatibles
	  * @return true if compatibles and incompatibles share an option common
	  */
	 private boolean hascommonElement(ArrayList<Option> compatibles,ArrayList<Option> incompatibles) {
		for(int i = 0; i< compatibles.size();i++) if (incompatibles.contains(compatibles.get(i)))return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Option> getCompatibles() {
		return (ArrayList<Option>) compatibles.clone();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Option> getIncompatibles() {
		return (ArrayList<Option>) incompatibles.clone();
	}

	public String getdescription(){
		 return description;
	 }
	 /**
	  * looks if an option can be in the same configuration as this option
	  * @param opt the other option
	  * @return true if the option can be in the same configuration as this option else no
	  */
	public Boolean conflictsWith(Option opt){
		if(this.getIncompatibles().contains(opt)) return true;
		if(this.getCompatibles().contains(opt)) return false;
		return opt.conflictsWith(this);
	}
	
	public String toString(){
		return description;
	}

	public abstract String getType();
	
	
}


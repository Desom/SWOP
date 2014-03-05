package Main;
import java.util.ArrayList;


public abstract class Option {
	 private String description; // 
	 private ArrayList<Option> compatibles;
	 private ArrayList<Option> incompatibles;
	 private final String OptionType;

	public Option(String description, ArrayList<Option> compatibles, ArrayList<Option> incompatibles,String type ) throws CarModelCatalogException{
		 if(description == null || compatibles==null || incompatibles==null || type == null) throw new CarModelCatalogException("null in non null value of Option");
		 if( hascommonElement(compatibles,incompatibles)) throw new CarModelCatalogException("Option is both Compatible and incompatiblle with another option at the same type" );
		 this.compatibles =compatibles;
		 this.incompatibles =incompatibles;
		 this.description =description;
		 this.OptionType = type;
	 }
	 
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
	 
	public Boolean conflictsWith(Option opt){
		if(this.getIncompatibles().contains(opt)) return true;
		if(this.getCompatibles().contains(opt)) return false;
		return opt.conflictsWith(this);
	}
	
	public String toString(){
		return description;
	}
	public String getType(){
		return OptionType;
	}
	
}


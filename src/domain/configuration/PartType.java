package domain.configuration;


public class PartType implements TaskType{
	
	protected String name;

	/**
	 * Constructor of PartType.
	 * 
	 * @param name
	 * 		The name of this PartType.
	 */
	PartType(String name){
		this.name = name;
	}
	
	/**
	 * Returns the name of this parttype.
	 * 
	 * @return the name of this parttype.
	 */
	public String getName(){
		return this.name;
	}
	
	public String toString(){
		return getName();
	}
	
	



}

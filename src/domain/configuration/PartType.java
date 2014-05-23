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
	 * Returns the name of this part type.
	 * 
	 * @return The name of this part type.
	 */
	public String getName(){
		return this.name;
	}
	
	public String toString(){
		return getName();
	}
	
	



}

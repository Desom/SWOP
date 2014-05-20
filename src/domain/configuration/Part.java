package domain.configuration;


public class Part implements Taskable {
	
	private String description; 
	private PartType type;
	
	/**
	 * Constructor of Part.
	 * 
	 * @param description
	 * 		The description of the part.
	 * @param type
	 * 		The type of this part.
	 * @throws VehicleModelCatalogException
	 * 		If the description is null.
	 * 		If the type is null.
	 */
	public Part(String description, PartType type) throws VehicleModelCatalogException {
		if(description == null || type == null)
			throw new VehicleModelCatalogException("null in non null value of Part");
		this.description = description;
		this.type = type;
	}
	
	
	/* (non-Javadoc)
	 * @see domain.configuration.Taskable#getDescription()
	 */
	@Override
	public String getDescription(){
		return description;
	}
	
	/* (non-Javadoc)
	 * @see domain.configuration.Taskable#getType()
	 */
	@Override
	public PartType getType(){
		return type;
	}
	
	@Override
	public String toString(){
		return description;
	}

}

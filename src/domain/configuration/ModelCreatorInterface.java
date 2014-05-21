package domain.configuration;

import java.io.IOException;
import java.util.ArrayList;

public interface ModelCreatorInterface {

	/**
	 * Creates the models from the file of the path of this model creator.
	 * 
	 * @return a list with all possible vehicle models
	 * @throws IOException
	 * @throws VehicleModelCatalogException
	 * 		If a model line is not in the right format.
	 */
	public abstract ArrayList<VehicleModel> createModels() throws IOException, VehicleModelCatalogException;

}
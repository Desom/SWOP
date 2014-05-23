package domain.configuration.models;

import java.io.IOException;
import java.util.ArrayList;

import domain.configuration.VehicleCatalogException;

public interface ModelCreatorInterface {

	/**
	 * Creates the models from the file of the path of this model creator.
	 * 
	 * @return A list with all possible vehicle models.
	 * @throws IOException
	 * @throws VehicleCatalogException
	 * 		If a model line is not in the right format.
	 */
	public abstract ArrayList<VehicleModel> createModels() throws IOException, VehicleCatalogException;

}
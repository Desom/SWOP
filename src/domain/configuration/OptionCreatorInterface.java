package domain.configuration;

import java.io.IOException;
import java.util.ArrayList;

public interface OptionCreatorInterface {

	/**
	 * Creates the options from the file.
	 * 
	 * @return all options contained in the file.
	 * @throws IOException
	 * @throws VehicleModelCatalogException
	 * 		If an option line is not in the right format.
	 */
	public abstract ArrayList<Option> createOptions() throws IOException, VehicleModelCatalogException;

	/**
	 * Creates the parts from the file.
	 * 
	 * @return all parts contained in the file.
	 * @throws IOException
	 * @throws VehicleModelCatalogException
	 * 		If a part line is not in the right format.
	 */
	public abstract ArrayList<Part> createParts() throws IOException, VehicleModelCatalogException;

}
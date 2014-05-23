package domain.assembly.assemblyline;

import java.util.ArrayList;

public interface AssemblyLineCreatorInterface {

	/**
	 * Creates all assembly lines with the imposed workstations.
	 * 
	 * @return All assembly lines of the company.
	 */
	public abstract ArrayList<AssemblyLine> create();

}
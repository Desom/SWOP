package domain.assembly.assemblyline;

public interface AssemblyLineObserver {
	
	/**
	 * React to a change in the assembly line.
	 */
	public void update();
}

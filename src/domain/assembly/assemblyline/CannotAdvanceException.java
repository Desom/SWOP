package domain.assembly.assemblyline;

import java.util.ArrayList;

import domain.assembly.workstations.Workstation;

public class CannotAdvanceException extends Exception{

	private static final long serialVersionUID = 1L;
	private ArrayList<Workstation> blockingWorkstations = new ArrayList<Workstation>();
	
	public CannotAdvanceException(ArrayList<Workstation> blockingWorkstations) {
		this.blockingWorkstations = blockingWorkstations;
	}
	
	/**
	 * Adds a workstation to the list of blocking workstations.
	 * 
	 * @param workstation
	 */
	public void addBlockingWorkstation(Workstation workstation){
		blockingWorkstations.add(workstation);
	}
	
	@Override
	public String getMessage(){
		String message = "The following workstations are not finished and are preventing the assemblyline from advancing:\n";
		for(Workstation w: blockingWorkstations){
			message += w.toString(); 
		}
		return message;
	}
}

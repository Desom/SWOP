package domain.assembly;

import java.util.LinkedList;

public class CannotAdvanceException extends Exception{


	private static final long serialVersionUID = 1L;
	private LinkedList<Workstation> blockingWorkstations = new LinkedList<Workstation>();
	
	public void addBlockingWorkstation(Workstation w){
		blockingWorkstations.add(w);
	}
	
	@Override
	public String getMessage(){
		String message = "The following workstations are not finished and are preventing the assemblyline from advancing:\n";
		for(Workstation w: blockingWorkstations){
			message += "ID: " + w.getId() + "\n"; 
		}
		return message;
	}

}

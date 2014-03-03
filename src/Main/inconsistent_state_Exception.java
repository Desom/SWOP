package Main;

public class inconsistent_state_Exception extends Exception {

	private final String Message;

	public inconsistent_state_Exception(String string) {
		Message=string;
	}
	
	public String GetMessage (){
		return Message;
		
	}
}

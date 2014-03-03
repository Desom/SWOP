package Main;

public class UserAccessException extends Exception{
	
	private static final long serialVersionUID = 1L;
	private final String Message;

	public UserAccessException(String string) {
		Message=string;
	}
	
	public String GetMessage (){
		return Message;
		
	}

}

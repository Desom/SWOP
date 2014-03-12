package User;

public class UserAccessException extends Exception{
	
	private static final long serialVersionUID = 1L;
	private final String Message;

	public UserAccessException(String string) {
		Message=string;
	}
	
	public UserAccessException(User user, String methodName) {
		Message="User ID " + user.getId() + " cannot excecute the method" +  methodName;
	}
	
	public String GetMessage (){
		return Message;
		
	}

}

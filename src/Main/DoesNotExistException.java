package Main;

public class DoesNotExistException extends Exception{


	private static final long serialVersionUID = 1L;
	private final String Message;

	public DoesNotExistException(String string) {
		Message=string;
	}

	public String GetMessage (){
		return Message;

	}

}


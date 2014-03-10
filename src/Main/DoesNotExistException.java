package Main;

public class DoesNotExistException extends Exception{


	private static final long serialVersionUID = 1L;
	private final String Message;

	public DoesNotExistException(String string) {
		Message=string;
	}
	
	@Override
	public String getMessage (){
		return Message;

	}

}


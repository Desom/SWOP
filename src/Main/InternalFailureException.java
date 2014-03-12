package Main;

public class InternalFailureException extends Exception{


	private static final long serialVersionUID = 1L;
	private final String Message;

	public InternalFailureException(String string) {
		Message=string;
	}
	
	@Override
	public String getMessage (){
		return Message;

	}

}


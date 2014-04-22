package domain;

public class InternalFailureException extends RuntimeException{


	private static final long serialVersionUID = 1L;

	public InternalFailureException(String message) {
		super(message);
	}


}


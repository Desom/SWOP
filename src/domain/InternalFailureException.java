package domain;

public class InternalFailureException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of InternalFailureException.
	 * @param message
	 * 		The message of this exception.
	 */
	public InternalFailureException(String message) {
		super(message);
	}


}


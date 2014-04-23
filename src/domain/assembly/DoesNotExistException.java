package domain.assembly;

public class DoesNotExistException extends Exception{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of DoesNotExistException
	 * 
	 * @param message
	 * 		The message explaining the exception.
	 */
	public DoesNotExistException(String message) {
		super(message);
	}
}


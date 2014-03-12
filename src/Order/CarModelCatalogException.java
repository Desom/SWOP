package Order;

public class CarModelCatalogException extends Exception {


	private static final long serialVersionUID = 1L;
	private final String Message;

	public CarModelCatalogException(String string) {
		Message=string;
	}
	
	public String GetMessage (){
		return Message;
		
	}
}

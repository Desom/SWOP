import java.io.IOException;


public class Main {

	/**
	 * @param args
	 * @throws inconsistent_state_Exception 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		try {
			new CarModelCatalog();
		} catch (inconsistent_state_Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.GetMessage());
		}
	}

}

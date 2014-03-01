import java.util.ArrayList;


public class User {
	
	private int id;
	ArrayList<String> approvedMethods;
	
	public User(int id) {
		this.setId(id);
		approvedMethods = new ArrayList<String>();
	}
	
	private void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean canPerform(String methodName) {
		return false;
	}

}

package Main;
import java.util.ArrayList;

public class User {
	
	// Vraag: wat als twee methodes hetzelfde heten?
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
		return this.approvedMethods.contains(methodName);
	}
}

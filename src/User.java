
public class User {
	
	private int id;
	
	public User(int id) {
		this.setId(id);
	}
	
	private void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public boolean canPerform(String methodName) {
		return true;
	}

}

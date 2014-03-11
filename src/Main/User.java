package Main;
import java.util.ArrayList;

public class User {
	
	private int id;
	ArrayList<String> approvedMethods;
	
	/**
	 * Constructor of User.
	 * 
	 * @param	id
	 * 			id of this user
	 */
	public User(int id) {
		this.setId(id);
		approvedMethods = new ArrayList<String>();
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param	id
	 * 			number that will be the id of this user
	 */
	private void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the id of this user.
	 * 
	 * @return	the id of this user
	 */
	public int getId() {
		return this.id;
	}
	/**
	 * Returns if this user is a car mechanic.
	 * 
	 * @return	True if this user is a car mechanic, otherwise false
	 */
	// Toegevoegd om te kunnen checken als de User die aan een Workstation wordt toegevoegd wel degelijk een car mechanic is.
	public boolean isCarMechanic() {
		return false;
	}
	
	/**
	 * Returns whether this user can perform the given method (represented by a string).
	 * 
	 * @param	methodName
	 * 			the name of the method this user wants to perform
	 * @return	true if this user can perform this method, otherwise false
	 */
	public boolean canPerform(String methodName) {
		return this.approvedMethods.contains(methodName);
	}
}

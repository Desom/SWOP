package domain.user;


public class User {
	
	private int id;
	
	/**
	 * Constructor of User.
	 * 
	 * @param	id
	 * 			id of this user
	 */
	public User(int id) {
		this.setId(id);
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
}

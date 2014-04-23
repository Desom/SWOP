package domain.user;

public class User {
	
	private int id;
	
	/**
	 * Constructor of User.
	 * 
	 * @param id
	 * 		Id of the new user.
	 */
	public User(int id) {
		this.setId(id);
	}
	
	/**
	 * Sets the id of this user.
	 * 
	 * @param id
	 * 		The id of this user.
	 */
	private void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the id of this user.
	 * 
	 * @return the id of this user
	 */
	public int getId() {
		return this.id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (this.id == other.id)
			return true;
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
}

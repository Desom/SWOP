package Test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import User.CarMechanic;
import User.GarageHolder;
import User.Manager;
import User.User;

public class UserTest {

	private static User user;
	private static Manager manager;
	private static CarMechanic carMechanic;
	private static GarageHolder garageHolder;
	
	@BeforeClass
	public static void testCreate() {
		user = new User(0);
		manager = new Manager(1);
		carMechanic = new CarMechanic(2);
		garageHolder = new GarageHolder(3);
		assertEquals(0, user.getId());
		assertEquals(1, manager.getId());
		assertEquals(2, carMechanic.getId());
		assertEquals(3, garageHolder.getId());
	}
}

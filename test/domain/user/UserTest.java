package domain.user;

import static org.junit.Assert.*;

import org.junit.Test;

import domain.user.Mechanic;
import domain.user.GarageHolder;
import domain.user.Manager;
import domain.user.User;

public class UserTest {

	private static User user;
	private static Manager manager;
	private static Mechanic carMechanic;
	private static GarageHolder garageHolder;
	
	@Test
	public void testCreate() {
		user = new User(0);
		manager = new Manager(1);
		carMechanic = new Mechanic(2);
		garageHolder = new GarageHolder(3);
		assertEquals(0, user.getId());
		assertEquals(1, manager.getId());
		assertEquals(2, carMechanic.getId());
		assertEquals(3, garageHolder.getId());
	}
}

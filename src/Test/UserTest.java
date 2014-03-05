package Test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import Main.CarMechanic;
import Main.GarageHolder;
import Main.Manager;
import Main.User;

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
	
	@Test
	public void testCanPerformUser() {
		assertFalse(user.canPerform("unexistingMethod"));
		assertFalse(user.canPerform("advanceLine"));
		assertFalse(user.canPerform("getAssemblyStatus"));
		assertFalse(user.canPerform("getOrders"));
		assertFalse(user.canPerform("placeOrder"));
		assertFalse(user.canPerform("selectWorkstation"));
		assertFalse(user.canPerform("completeTask"));
	}
	
	@Test
	public void testCanPerformManager() {
		assertFalse(manager.canPerform("unexistingMethod"));
		assert(manager.canPerform("advanceLine"));
		assert(manager.canPerform("hasAllTasksCompleted"));
		assert(manager.canPerform("getAssemblyStatus"));
		assert(manager.canPerform("getFutureStatus"));
	}
	
	@Test
	public void testCanPerformCarMechanic() {
		assertFalse(carMechanic.canPerform("unexistingMethod"));
		assert(carMechanic.canPerform("selectWorkstation"));
		assert(carMechanic.canPerform("getAllPendingTasks"));
		assert(carMechanic.canPerform("selectTask"));
		assert(carMechanic.canPerform("getActiveTaskInformation"));
		assert(carMechanic.canPerform("completeTask"));
	}
	
	@Test
	public void testCanPerformGarageHolder() {
		assertFalse(garageHolder.canPerform("unexistingMethod"));
		assert(garageHolder.canPerform("getOrders"));
		assert(garageHolder.canPerform("getAllModels"));
		assert(garageHolder.canPerform("placeOrder"));
		assert(garageHolder.canPerform("getCompletionEstimate"));
	}

}

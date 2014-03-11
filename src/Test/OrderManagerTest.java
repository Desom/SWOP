package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import Main.*;


public class OrderManagerTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public OrderManager orderManager;
	private static CarModelCatalog catalog;
	private static GarageHolder user1;
	private static GarageHolder user2;
	private static GarageHolder user3;
	private static Manager manager;
	private static CarMechanic mechanic;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		catalog = new CarModelCatalog();
		user1 = new GarageHolder(1);
		user2 = new GarageHolder(2);
		user3 = new GarageHolder(3);
		manager = new Manager(4);
		mechanic = new CarMechanic(5);
	}
	
	@Before
	public void setUp() throws Exception {
		orderManager = new OrderManager("testData_OrderManager.txt", catalog);
	}

	@Test
	public void testCreation() {
		
	}

	@Test
	public void testGetOrders() throws UserAccessException {
		ArrayList<CarOrder> orders1 = orderManager.getOrders(user1);
		ArrayList<CarOrder> orders2 = orderManager.getOrders(user2);
		ArrayList<CarOrder> orders3 = orderManager.getOrders(user3);
		
		assertEquals(1,orders1.size());
		assertEquals(3,orders2.size());
		assertEquals(0,orders3.size());
		
		assertEquals(1,orders1.get(0).getCarOrderID());
		assertEquals(2,orders2.get(0).getCarOrderID());
		assertEquals(3,orders2.get(1).getCarOrderID());
		assertEquals(4,orders2.get(2).getCarOrderID());
	}

	@Test
	public void testGetOrdersUserAccess() throws UserAccessException {

		ArrayList<CarOrder> orders1 = orderManager.getOrders(user1);

		exception.expect(UserAccessException.class);
		orderManager.getOrders(manager);
		//TODO goed genoeg zo?
		orderManager.getOrders(mechanic);
	}

	@Test
	public void testPlaceOrder() {
		fail("Not yet implemented");
	}

	@Test
	public void testCompletionEstimate() throws UserAccessException {
		fail("Not yet implemented");
		ArrayList<CarOrder> orders1 = orderManager.getOrders(user1);
		ArrayList<CarOrder> orders2 = orderManager.getOrders(user2);

	}

}

package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import Car.CarOrder;
import Order.CarModelCatalog;
import Order.OrderManager;
import Order.OurOrderform;
import User.CarMechanic;
import User.GarageHolder;
import User.Manager;
import User.UserAccessException;


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
		orderManager = new OrderManager("testData_OrderManager.txt", catalog, new GregorianCalendar(2014, 1, 1,12,0,0));
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

		orderManager.getOrders(user1);

		exception.expect(UserAccessException.class);
		orderManager.getOrders(manager);
		//TODO goed genoeg zo?
		orderManager.getOrders(mechanic);
	}

	@Test
	public void testPlaceOrderUserAccess(){
		OurOrderform form = new OurOrderform(manager, catalog.getCarModel("Ford"), catalog);
		form.setOption("manual");
		form.setOption("sedan");
		form.setOption("red");
		form.setOption("performance 2.5l 6 cilinders");
		form.setOption("6 speed manual");
		form.setOption("leather black");
		form.setOption("comfort");
		
		try {
			orderManager.placeOrder(form);
			fail();
		} catch (UserAccessException e) {
			assertEquals(e.getMessage(),"User ID " + 4 + " cannot excecute the method " + "placeOrder");
		}

		OurOrderform form2 = new OurOrderform(mechanic, catalog.getCarModel("Ford"), catalog);
		form2.setOption("manual");
		form2.setOption("sedan");
		form2.setOption("red");
		form2.setOption("performance 2.5l 6 cilinders");
		form2.setOption("6 speed manual");
		form2.setOption("leather black");
		form2.setOption("comfort");
		
		try {
			orderManager.placeOrder(form2);
			fail();
		} catch (UserAccessException e) {
			assertEquals("User ID " + 5 + " cannot excecute the method " + "placeOrder",e.getMessage());
		}
	}
	
	@Test
	public void testPlaceOrder() throws UserAccessException {
		OurOrderform form = new OurOrderform(user3, catalog.getCarModel("Ford"), catalog);
		form.setOption("manual");
		form.setOption("sedan");
		form.setOption("red");
		form.setOption("performance 2.5l 6 cilinders");
		form.setOption("6 speed manual");
		form.setOption("leather black");
		form.setOption("comfort");
		
		orderManager.placeOrder(form);
		ArrayList<CarOrder> orders = orderManager.getOrders(user3);
		assertEquals(5,orders.get(0).getCarOrderID());
		assertEquals(3,orders.get(0).getUserId());
	}

	@Test
	public void testCompletionEstimate() throws UserAccessException {
		ArrayList<CarOrder> orders1 = orderManager.getOrders(user1);
		GregorianCalendar cal = orderManager.completionEstimate(user1, orders1.get(0));
		assertEquals(2013,orderManager.completionEstimate(user1, orders1.get(0)).get(GregorianCalendar.YEAR));
		ArrayList<CarOrder> orders2 = orderManager.getOrders(user2);

		CarOrder order2_0 = orders2.get(0);
		assertTrue(order2_0.toString().startsWith("CarOrder: 2"));
		assertEquals(15,orderManager.completionEstimate(user2, order2_0).get(GregorianCalendar.HOUR_OF_DAY));
		CarOrder order2_1 = orders2.get(1);
		assertTrue(order2_1.toString().startsWith("CarOrder: 3"));
		assertEquals(16,orderManager.completionEstimate(user2, order2_1).get(GregorianCalendar.HOUR_OF_DAY));
		CarOrder order2_2 = orders2.get(2);
		assertTrue(order2_2.toString().startsWith("CarOrder: 4"));
		assertEquals(17,orderManager.completionEstimate(user2, order2_2).get(GregorianCalendar.HOUR_OF_DAY));
	}
	
	@Test
	public void testCompletionEstimateUserAccess() throws UserAccessException{
		ArrayList<CarOrder> orders2 = orderManager.getOrders(user2);
		CarOrder order2_0 = orders2.get(0);
		
		ArrayList<String> compOrder1;
		GregorianCalendar estimate;
		try {
			estimate = orderManager.completionEstimate(manager,order2_0);
			fail();
		} catch (UserAccessException e) {
			assertEquals("User ID " + 4 + " cannot excecute the method " + "completionEstimate",e.getMessage());
		}
		
		try {
			estimate = orderManager.completionEstimate(mechanic,order2_0);
			fail();
		} catch (UserAccessException e) {
			assertEquals("User ID " + 5 + " cannot excecute the method " + "completionEstimate",e.getMessage());
		}
	}
	
	
	@Test
	public void testGetPendingOrders() throws UserAccessException {
		ArrayList<CarOrder> pendOrder1 = orderManager.getPendingOrders(user1);
		assertEquals(0,pendOrder1.size());

		ArrayList<CarOrder> pendOrder2 = orderManager.getPendingOrders(user2);
		assertEquals(3,pendOrder2.size());
		assertEquals(2,pendOrder2.get(0).getCarOrderID());
		assertEquals(3,pendOrder2.get(1).getCarOrderID());
		assertEquals(4,pendOrder2.get(2).getCarOrderID());

		ArrayList<CarOrder> pendOrder3 = orderManager.getPendingOrders(user3);
		assertEquals(0,pendOrder3.size());
	}
	
	@Test
	public void testGetPendingOrdersUserAccess(){
		ArrayList<CarOrder> compOrder1;
		try {
			compOrder1 = orderManager.getPendingOrders(manager);
			fail();
		} catch (UserAccessException e) {
			assertEquals("User ID " + 4 + " cannot excecute the method " + "getPendingOrders",e.getMessage());
		}
		
		try {
			compOrder1 = orderManager.getPendingOrders(mechanic);
			fail();
		} catch (UserAccessException e) {
			assertEquals("User ID " + 5 + " cannot excecute the method " + "getPendingOrders",e.getMessage());
		}
	}
	
	@Test
	public void testGetCompletedOrders() throws UserAccessException {
		ArrayList<CarOrder> compOrder1 = orderManager.getCompletedOrders(user1);
		assertEquals(1,compOrder1.size());
		
		assertEquals(1,compOrder1.get(0).getCarOrderID());
		
		ArrayList<CarOrder> compOrder2 = orderManager.getCompletedOrders(user2);
		assertEquals(0,compOrder2.size());
		ArrayList<CarOrder> compOrder3 = orderManager.getCompletedOrders(user3);
		assertEquals(0,compOrder3.size());
	}
	
	@Test
	public void testGetCompletedOrdersUserAccess(){
		ArrayList<CarOrder> compOrder1;
		try {
			compOrder1 = orderManager.getCompletedOrders(manager);
			fail();
		} catch (UserAccessException e) {
			assertEquals("User ID " + 4 + " cannot excecute the method " + "getCompletedOrders",e.getMessage());
		}
		
		try {
			compOrder1 = orderManager.getCompletedOrders(mechanic);
			fail();
		} catch (UserAccessException e) {
			assertEquals("User ID " + 5 + " cannot excecute the method " + "getCompletedOrders",e.getMessage());
		}
	}
}

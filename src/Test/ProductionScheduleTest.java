package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Main.*;

public class ProductionScheduleTest {
	public ArrayList<CarOrder> carOrders;
	public static CarOrder carOrder;
	public static CarOrder carOrder1;
	public ProductionSchedule prodSched;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar now1 = new GregorianCalendar();
		now1.add(now1.HOUR_OF_DAY, 1);
		carOrder = new CarOrder(0, 0, now, null, null, null);
		carOrder1 = new CarOrder(1, 0, now, null, null, null);
	}
	
	@Before
	public void setUp() throws Exception {

		carOrders = new ArrayList<CarOrder>();
		carOrders.add(carOrder);
		carOrders.add(carOrder1);
		prodSched = new ProductionSchedule(carOrders);
	}
	
	
	
	@Test
	public void testCreation() {
		fail("Not yet implemented");
	}

	@Test
	public void testCompletionEstimate() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddOrder() {

		GregorianCalendar now2 = new GregorianCalendar();
		now2.add(now2.HOUR_OF_DAY, 2);
		CarOrder order2 = new CarOrder(2, 0, now2, null, null, null);
		prodSched.addOrder(order2);
		prodSched.getNextCarOrder(0);
		prodSched.getNextCarOrder(1);
		CarOrder lastOrder = prodSched.getNextCarOrder(2);
		assertEquals(2,lastOrder.getCarOrderID());
	}

	@Test
	public void testSeeNextCarOrder() {
		CarOrder a = prodSched.seeNextCarOrder();
		CarOrder b = prodSched.seeNextCarOrder();

		assertEquals(0,a.getCarOrderID());
		assertEquals(0,b.getCarOrderID());
		
		prodSched.getNextCarOrder(0);
		CarOrder c = prodSched.seeNextCarOrder();
		assertEquals(1,c.getCarOrderID());
	}

	@Test
	public void testGetNextCarOrder() {
		CarOrder a = prodSched.getNextCarOrder(0);
		assertEquals(0,a.getCarOrderID());
		
		CarOrder b = prodSched.getNextCarOrder(0);
		assertEquals(1,b.getCarOrderID());
		
		prodSched.getNextCarOrder(0);
		CarOrder c = prodSched.seeNextCarOrder();
		assertNull(c);
	}

}

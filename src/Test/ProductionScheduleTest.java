package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Main.*;
import OptionSubTypes.*;

public class ProductionScheduleTest {
	public ArrayList<CarOrder> carOrders;
	public static CarOrder carOrder;
	public static CarOrder carOrder1;
	public ProductionSchedule prodSched;
	public static ArrayList<Option> arrayList;
	public static Airco A;
	public static Body B;
	public static Color C;
	public static Engine E; 
	public static Gearbox G;
	public static Seats S;
	public static Wheels W;
	public static String Name;
	public static CarModel carModel;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		arrayList = new ArrayList<Option>();
		A = new Airco("a",arrayList,arrayList);
		B = new Body("b",arrayList,arrayList);
		C = new Color("c",arrayList,arrayList);
		E = new Engine("e",arrayList,arrayList);
		G = new Gearbox("g",arrayList,arrayList);
		S = new Seats("s",arrayList,arrayList);
		W = new Wheels("w",arrayList,arrayList);
		Name = "BMW";
		carModel = new CarModel(Name,arrayList,A,B,C,E,G,S,W);
		
		
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar now1 = new GregorianCalendar();
		now1.add(now1.HOUR_OF_DAY, 1);
		carOrder = new CarOrder(0, 0, now, null, carModel, new ArrayList<Option>());
		carOrder1 = new CarOrder(1, 0, now, null, carModel, new ArrayList<Option>());
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
		//problemen met het feit dat er geen carOrders komen na de werkdag.
		GregorianCalendar now2 = new GregorianCalendar();
		now2.add(now2.HOUR_OF_DAY, 2);
		CarOrder order2 = new CarOrder(2, 0, now2, null,  carModel, new ArrayList<Option>());
		prodSched.addOrder(order2);
		prodSched.getNextCarOrder(0);
		prodSched.getNextCarOrder(1);
		CarOrder lastOrder = prodSched.getNextCarOrder(2);
		assertEquals(2,lastOrder.getCarOrderID());
	}

	@Test
	public void testSeeNextCarOrder() {
		//problemen met het feit dat er geen carOrders komen na de werkdag.
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
		//problemen met het feit dat er geen carOrders komen na de werkdag.
		CarOrder a = prodSched.getNextCarOrder(0);
		assertEquals(0,a.getCarOrderID());
		
		CarOrder b = prodSched.getNextCarOrder(0);
		assertEquals(1,b.getCarOrderID());
		
		prodSched.getNextCarOrder(0);
		CarOrder c = prodSched.seeNextCarOrder();
		assertNull(c);
	}

}

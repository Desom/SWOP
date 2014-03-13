package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Car.CarModel;
import Car.CarOrder;
import Car.Option;
import Order.CarModelCatalog;
import User.GarageHolder;
import User.Manager;
import User.UserAccessException;

public class CarOrderTest {

	public static ArrayList<Option> arrayOption;
	public static CarModel carModel;
	public static GarageHolder garageHolder;
	public static Manager manager;

	public CarOrder carOrder;
	public CarOrder carOrder1;
	public CarOrder carOrder2;
	public CarOrder carOrderNew;
	public GregorianCalendar now;
	public GregorianCalendar now1;
	public GregorianCalendar now3;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CarModelCatalog catalog = new CarModelCatalog();
		carModel = catalog.getCarModel("Ford");
		arrayOption = new ArrayList<Option>();
		arrayOption.add(catalog.getOption("sedan"));
		arrayOption.add(catalog.getOption("manual"));
		arrayOption.add(catalog.getOption("blue"));
		arrayOption.add(catalog.getOption("performance 2.5l 6 cilinders"));
		arrayOption.add(catalog.getOption("6 speed manual"));
		arrayOption.add(catalog.getOption("leather black"));
		arrayOption.add(catalog.getOption("comfort"));

		garageHolder = new GarageHolder(50);
		manager = new Manager(51);
	}

	@Before
	public void setUp() throws Exception {
		
		now = new GregorianCalendar();
		now1 = (GregorianCalendar) now.clone();
		now1.add(GregorianCalendar.HOUR_OF_DAY, 1);
		now3 = (GregorianCalendar) now.clone();
		now3.add(GregorianCalendar.HOUR_OF_DAY, 3);
		carOrder = new CarOrder(0, 0, now, now3, carModel, arrayOption);
		carOrder1 = new CarOrder(1, 0, now1, null, carModel, arrayOption);
		carOrder2 = new CarOrder(3, 0, now3, null, carModel, arrayOption);
		carOrderNew = new CarOrder(5, garageHolder,carModel, arrayOption);
	}

	@Test
	public void testCreation() {
		assertEquals(carModel,carOrder.getCar().getConfiguration().getModel());
		assertEquals(0,carOrder.getCarOrderID());
		assertEquals(now3,carOrder.getDeliveredTime());
		assertEquals(now,carOrder.getOrderedTime());
		assertEquals(0,carOrder.getUserId());
	}

	@Test
	public void testGetDeliveredTime() {
		assertEquals(now3, carOrder.getDeliveredTime());
		
		try{
			carOrder1.getDeliveredTime();
			fail();
		}
		catch(IllegalStateException e){
			assertEquals("This car hasn't been delivered yet",e.getMessage());
		}
		
		try{
			carOrder2.getDeliveredTime();
			fail();
		}
		catch(IllegalStateException e){
			assertEquals("This car hasn't been delivered yet",e.getMessage());
		}
		
		try{
			carOrderNew.getDeliveredTime();
			fail();
		}
		catch(IllegalStateException e){
			assertEquals("This car hasn't been delivered yet",e.getMessage());
		}
	}

	@Test
	public void testSetDeliveredTime() {
		GregorianCalendar now4 = (GregorianCalendar) now.clone();
		now3.add(GregorianCalendar.HOUR_OF_DAY, 24);
		
		
		try {
			carOrder1.setDeliveredTime(manager, now4);
			fail();
		} catch (IllegalStateException e) {
			assertEquals("Can't set deliveredTime because this CarOrder is not completed yet.",e.getMessage());
		} catch (UserAccessException e) {
			fail();
		}
		
		try {
			carOrder2.setDeliveredTime(manager, now4);
		} catch (IllegalStateException e) {
			assertEquals("Can't set deliveredTime because this CarOrder is not completed yet.",e.getMessage());
		} catch (UserAccessException e) {
			fail();
		}
		
		try {
			carOrder.setDeliveredTime(manager, now4);
		} catch (IllegalStateException e) {
			fail();
		} catch (UserAccessException e) {
			fail();
		}
	}

}


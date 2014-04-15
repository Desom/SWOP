package domain.order;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.Option;
import domain.order.CarOrder;
import domain.user.GarageHolder;

public class CarOrderTest {

	public static ArrayList<Option> arrayOption;
	public static CarModel carModel;
	public static GarageHolder garageHolder;

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
		carModel = null;
		for(CarModel m : catalog.getAllModels()){
			if(m.getName().equals("Model A")){
				carModel = m;
				continue;
			}
		}
		arrayOption = new ArrayList<Option>();
		for(Option option : catalog.getAllOptions()){
			if(option.getDescription().equals("sedan")
					||option.getDescription().equals("blue")
					||option.getDescription().equals("standard 2l v4")
					||option.getDescription().equals("5 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("no airco")
					||option.getDescription().equals("comfort")
					||option.getDescription().equals("no spoiler")
					)
				arrayOption.add(option);
		}
		
		garageHolder = new GarageHolder(1);
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
		//TODO meer testen?
		assertEquals(carModel,carOrder.getOrder().getConfiguration().getModel());
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
			carOrder1.setDeliveredTime(now4);
			fail();
		} catch (IllegalStateException e) {
			assertEquals("Can't set deliveredTime because this CarOrder is not completed yet.",e.getMessage());
		}
		try {
			carOrder2.setDeliveredTime(now4);
		} catch (IllegalStateException e) {
			assertEquals("Can't set deliveredTime because this CarOrder is not completed yet.",e.getMessage());
		}
		
		try {
			carOrder.setDeliveredTime(now4);
		} catch (IllegalStateException e) {
			fail();
		}
		
	}

}


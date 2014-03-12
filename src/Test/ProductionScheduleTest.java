package Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Assembly.ProductionSchedule;
import Car.CarModel;
import Car.CarOrder;
import Car.Option;
import Main.*;
import Order.CarModelCatalog;
import Order.OptionSubTypes.*;

public class ProductionScheduleTest {
	public ArrayList<CarOrder> carOrders;
	public static CarOrder carOrder;
	public static CarOrder carOrder1;
	public static CarOrder carOrderLaatst;
	public ProductionSchedule prodSchedNormal;
	public ProductionSchedule prodSchedEarly;
	public ProductionSchedule prodSchedLate;
	public static CarModel carModel;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CarModelCatalog catalog = new CarModelCatalog();
		carModel = catalog.getCarModel("Ford");
		ArrayList<Option> arrayOption = carModel.getOptions();
		
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar now1 = new GregorianCalendar();
		now1.add(GregorianCalendar.HOUR_OF_DAY, 1);
		GregorianCalendar now3 = new GregorianCalendar();
		now3.add(GregorianCalendar.HOUR_OF_DAY, 3);
		carOrder = new CarOrder(0, 0, now, null, carModel, arrayOption);
		carOrder1 = new CarOrder(1, 0, now1, null, carModel, arrayOption);
		carOrderLaatst = new CarOrder(2, 0, now3, null, carModel, arrayOption);
	}
	
	@Before
	public void setUp() throws Exception {

		GregorianCalendar now2 = new GregorianCalendar();
		now2.add(GregorianCalendar.HOUR_OF_DAY, 2);
		carOrders = new ArrayList<CarOrder>();
		carOrders.add(carOrder); //1
		carOrders.add(carOrder1);//2
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//3
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//4
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//5
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//6
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//7
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//8
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//9
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//10
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//11
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//12
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//13
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//14
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//15
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//16
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//17
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//18
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//19
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//20
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//21
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//22
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//23
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//24
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//25
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//26
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//27
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//28
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//29
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//30
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//31
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, new ArrayList<Option>()));//32
		carOrders.add(carOrderLaatst);//33
		
		GregorianCalendar day = new GregorianCalendar();
		day.set(GregorianCalendar.DAY_OF_YEAR, 100);
		day.set(GregorianCalendar.HOUR_OF_DAY, 12);
		day.set(GregorianCalendar.MINUTE, 0);
		day.set(GregorianCalendar.SECOND, 0);
		day.set(GregorianCalendar.MILLISECOND, 0);
		prodSchedNormal = new ProductionSchedule(carOrders, day);
		GregorianCalendar early = new GregorianCalendar();
		early.set(GregorianCalendar.DAY_OF_YEAR, 100);
		early.set(GregorianCalendar.HOUR_OF_DAY, 5);
		early.set(GregorianCalendar.MINUTE, 5);
		early.set(GregorianCalendar.SECOND, 9);
		early.set(GregorianCalendar.MILLISECOND, 10);
		prodSchedEarly = new ProductionSchedule(carOrders, early);
		GregorianCalendar late = new GregorianCalendar();
		late.set(GregorianCalendar.DAY_OF_YEAR, 100);
		late.set(GregorianCalendar.HOUR_OF_DAY, 20);
		late.set(GregorianCalendar.MINUTE, 1);
		late.set(GregorianCalendar.SECOND, 2);
		late.set(GregorianCalendar.MILLISECOND, 3);
		prodSchedLate = new ProductionSchedule(carOrders, late);
	}
	
	
	
	@Test
	public void testCreation() {
		fail("Not yet implemented");
	}

	@Test
	public void testCompletionEstimate() {
		//test normaal tijdstip
		GregorianCalendar estimate1 = prodSchedNormal.completionEstimateCarOrder(carOrder);
		assertEquals(100,estimate1.get(GregorianCalendar.DAY_OF_YEAR));
		assertEquals(15,estimate1.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(0,estimate1.get(GregorianCalendar.MINUTE));
		assertEquals(0,estimate1.get(GregorianCalendar.SECOND));
		assertEquals(0,estimate1.get(GregorianCalendar.MILLISECOND));
		
		GregorianCalendar estimate2 = prodSchedNormal.completionEstimateCarOrder(carOrder1);
		assertEquals(100,estimate2.get(GregorianCalendar.DAY_OF_YEAR));
		assertEquals(16,estimate2.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(0,estimate2.get(GregorianCalendar.MINUTE));
		assertEquals(0,estimate2.get(GregorianCalendar.SECOND));
		assertEquals(0,estimate2.get(GregorianCalendar.MILLISECOND));

		GregorianCalendar estimateL1 = prodSchedNormal.completionEstimateCarOrder(carOrderLaatst);
		assertEquals(102,estimateL1.get(GregorianCalendar.DAY_OF_YEAR));
		assertEquals(19,estimateL1.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(0,estimateL1.get(GregorianCalendar.MINUTE));
		assertEquals(0,estimateL1.get(GregorianCalendar.SECOND));
		assertEquals(0,estimateL1.get(GregorianCalendar.MILLISECOND));
		
		//test voor de dag begint tijdstip
		GregorianCalendar estimate3 = prodSchedEarly.completionEstimateCarOrder(carOrder);
		assertEquals(100,estimate3.get(GregorianCalendar.DAY_OF_YEAR));
		assertEquals(9,estimate3.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(0,estimate3.get(GregorianCalendar.MINUTE));
		assertEquals(0,estimate3.get(GregorianCalendar.SECOND));
		assertEquals(0,estimate3.get(GregorianCalendar.MILLISECOND));

		GregorianCalendar estimate4 = prodSchedEarly.completionEstimateCarOrder(carOrder1);
		assertEquals(100,estimate4.get(GregorianCalendar.DAY_OF_YEAR));
		assertEquals(10,estimate4.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(0,estimate4.get(GregorianCalendar.MINUTE));
		assertEquals(0,estimate4.get(GregorianCalendar.SECOND));
		assertEquals(0,estimate4.get(GregorianCalendar.MILLISECOND));

		GregorianCalendar estimateL2 = prodSchedEarly.completionEstimateCarOrder(carOrderLaatst);
		assertEquals(102,estimateL2.get(GregorianCalendar.DAY_OF_YEAR));
		assertEquals(13,estimateL2.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(0,estimateL2.get(GregorianCalendar.MINUTE));
		assertEquals(0,estimateL2.get(GregorianCalendar.SECOND));
		assertEquals(0,estimateL2.get(GregorianCalendar.MILLISECOND));
		
		//test aan het einde van de dag tijdstip
		GregorianCalendar estimate5 = prodSchedLate.completionEstimateCarOrder(carOrder);
		assertEquals(101,estimate5.get(GregorianCalendar.DAY_OF_YEAR));
		assertEquals(9,estimate5.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(0,estimate5.get(GregorianCalendar.MINUTE));
		assertEquals(0,estimate5.get(GregorianCalendar.SECOND));
		assertEquals(0,estimate5.get(GregorianCalendar.MILLISECOND));

		GregorianCalendar estimate6 = prodSchedLate.completionEstimateCarOrder(carOrder1);
		assertEquals(101,estimate6.get(GregorianCalendar.DAY_OF_YEAR));
		assertEquals(10,estimate6.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(0,estimate6.get(GregorianCalendar.MINUTE));
		assertEquals(0,estimate6.get(GregorianCalendar.SECOND));
		assertEquals(0,estimate6.get(GregorianCalendar.MILLISECOND));

		GregorianCalendar estimateL3 = prodSchedLate.completionEstimateCarOrder(carOrderLaatst);
		assertEquals(103,estimateL3.get(GregorianCalendar.DAY_OF_YEAR));
		assertEquals(13,estimateL3.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(0,estimateL3.get(GregorianCalendar.MINUTE));
		assertEquals(0,estimateL3.get(GregorianCalendar.SECOND));
		assertEquals(0,estimateL3.get(GregorianCalendar.MILLISECOND));
	}

	@Test
	public void testAddOrder() {
		//problemen met het feit dat er geen carOrders komen na de werkdag.
		GregorianCalendar now2 = new GregorianCalendar();
		now2.add(now2.HOUR_OF_DAY, 2);
		CarOrder order2 = new CarOrder(2, 0, now2, null,  carModel, new ArrayList<Option>());
		prodSchedNormal.addOrder(order2);
		prodSchedNormal.getNextCarOrder(0);
		prodSchedNormal.getNextCarOrder(1);
		CarOrder lastOrder = prodSchedNormal.getNextCarOrder(2);
		assertEquals(2,lastOrder.getCarOrderID());
	}

	@Test
	public void testSeeNextCarOrder() {
		//problemen met het feit dat er geen carOrders komen na de werkdag.
		CarOrder a = prodSchedNormal.seeNextCarOrder();
		CarOrder b = prodSchedNormal.seeNextCarOrder();

		assertEquals(0,a.getCarOrderID());
		assertEquals(0,b.getCarOrderID());
		
		prodSchedNormal.getNextCarOrder(0);
		CarOrder c = prodSchedNormal.seeNextCarOrder();
		assertEquals(1,c.getCarOrderID());
	}

	@Test
	public void testGetNextCarOrder() {
		//problemen met het feit dat er geen carOrders komen na de werkdag.
		CarOrder a = prodSchedNormal.getNextCarOrder(60);
		assertEquals(0,a.getCarOrderID());
		
		CarOrder b = prodSchedNormal.getNextCarOrder(60);
		assertEquals(1,b.getCarOrderID());
		
		for(int i = 6; i >0;i++){
			CarOrder c = prodSchedNormal.getNextCarOrder(60);
			assertEquals(2,c.getCarOrderID());
		}
		for(int i = 3; i >0;i++){
			CarOrder c = prodSchedNormal.getNextCarOrder(60);
			assertNull(c);
		}

		CarOrder c = prodSchedNormal.getNextCarOrder(60);
		assertEquals(2,c.getCarOrderID());
	}

}

package test.assembly;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import domain.assembly.ProductionSchedule;
import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.Option;
import domain.order.CarOrder;

public class ProductionScheduleTest {
	public ArrayList<CarOrder> carOrders;
	public static CarOrder carOrder;
	public static CarOrder carOrder1;
	public static CarOrder carOrderLaatst;
	public ProductionSchedule prodSchedNormal;
	public ProductionSchedule prodSchedEarly;
	public ProductionSchedule prodSchedLate;
	public static CarModel carModel;
	public static ArrayList<Option> arrayOption;
	
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
		
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar now1 = new GregorianCalendar();
		now1.add(GregorianCalendar.HOUR_OF_DAY, 1);
		GregorianCalendar now3 = new GregorianCalendar();
		now3.add(GregorianCalendar.HOUR_OF_DAY, 3);
		carOrder = new CarOrder(0, 0, now, null, carModel, arrayOption);
		carOrder1 = new CarOrder(1, 0, now1, null, carModel, arrayOption);
		carOrderLaatst = new CarOrder(3, 0, now3, null, carModel, arrayOption);
	}
	
	@Before
	public void setUp() throws Exception {

		GregorianCalendar now2 = new GregorianCalendar();
		now2.add(GregorianCalendar.HOUR_OF_DAY, 2);
		carOrders = new ArrayList<CarOrder>();
		carOrders.add(carOrder); //1
		carOrders.add(carOrder1);//2
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//3
		carOrders.add(new CarOrder(2, 0, now2, null, carModel,arrayOption));//4
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//5
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//6
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//7
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//8
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//9
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//10
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//11
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//12
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//13
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//14
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//15
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//16
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//17
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//18
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//19
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//20
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//21
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//22
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//23
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//24
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//25
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//26
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//27
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//28
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//29
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//30
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//31
		carOrders.add(new CarOrder(2, 0, now2, null, carModel, arrayOption));//32
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
	public void testAddOrder(){
		//problemen met het feit dat er geen carOrders komen na de werkdag.
		GregorianCalendar now2 = new GregorianCalendar();
		now2.add(Calendar.HOUR_OF_DAY, 2);
		CarOrder order2 = new CarOrder(2, 0, now2, null,  carModel, arrayOption);
		prodSchedNormal.addOrder(order2);
		prodSchedNormal.getNextCarOrder(0);
		prodSchedNormal.getNextCarOrder(1);
		CarOrder lastOrder = prodSchedNormal.getNextCarOrder(2);
		assertEquals(2,lastOrder.getCarOrderID());
	}

	@Test
	public void testSeeNextCarOrder(){
		//problemen met het feit dat er geen carOrders komen na de werkdag.
		CarOrder a2 = prodSchedNormal.seeNextCarOrder(0);
		CarOrder a = prodSchedNormal.getNextCarOrder(0);
		assertEquals(a,a2);

		CarOrder b2 = prodSchedNormal.seeNextCarOrder(60);
		CarOrder b = prodSchedNormal.getNextCarOrder(60);
		assertEquals(b,b2);
		
		for(int i = 6; i >0;i--){
			CarOrder c2 = prodSchedNormal.seeNextCarOrder(60);
			CarOrder c = prodSchedNormal.getNextCarOrder(60);
			assertEquals(c,c2);
		}
		for(int i = 3; i >0;i--){
			CarOrder c2 = prodSchedNormal.seeNextCarOrder(60);
			CarOrder c = prodSchedNormal.getNextCarOrder(60);
			assertEquals(c,c2);
			assertNull(c2);
		}
		
		a2 = prodSchedNormal.seeNextCarOrder(0);
		a = prodSchedNormal.getNextCarOrder(0);
		assertEquals(a,a2);

		for(int i = 13; i >0;i--){
			CarOrder c2 = prodSchedNormal.seeNextCarOrder(60);
			CarOrder c = prodSchedNormal.getNextCarOrder(60);
			assertEquals(c,c2);
		}
		for(int i = 3; i >0;i--){
			CarOrder c2 = prodSchedNormal.seeNextCarOrder(60);
			CarOrder c = prodSchedNormal.getNextCarOrder(60);
			assertEquals(c,c2);
			assertNull(c2);
		}
		
		a = prodSchedNormal.getNextCarOrder(0);
		assertEquals(2,a.getCarOrderID());
		
		for(int i = 9; i >0;i--){
			CarOrder c2 = prodSchedNormal.seeNextCarOrder(60);
			CarOrder c = prodSchedNormal.getNextCarOrder(60);
			assertEquals(c,c2);
		}
		
		CarOrder c2 = prodSchedNormal.seeNextCarOrder(60);
		CarOrder c = prodSchedNormal.getNextCarOrder(60);
		assertEquals(c,c2);
		assertEquals(3,c.getCarOrderID());
	}
	

	@Test
	public void testgetNextCarOrder(){
		CarOrder a = prodSchedNormal.getNextCarOrder(0);
		assertEquals(0,a.getCarOrderID());
		
		CarOrder b = prodSchedNormal.getNextCarOrder(60);
		assertEquals(1,b.getCarOrderID());
		
		for(int i = 6; i >0;i--){
			CarOrder c = prodSchedNormal.getNextCarOrder(60);
			assertEquals(2,c.getCarOrderID());
		}
		for(int i = 3; i >0;i--){
			CarOrder c = prodSchedNormal.getNextCarOrder(60);
			assertNull(c);
		}
		
		a = prodSchedNormal.getNextCarOrder(0);
		assertEquals(2,a.getCarOrderID());
		
		for(int i = 13; i >0;i--){
			CarOrder c = prodSchedNormal.getNextCarOrder(60);
			assertEquals(2,c.getCarOrderID());
		}
		for(int i = 3; i >0;i--){
			CarOrder c = prodSchedNormal.getNextCarOrder(60);
			assertNull(c);
		}
		
		a = prodSchedNormal.getNextCarOrder(0);
		assertEquals(2,a.getCarOrderID());
		
		for(int i = 9; i >0;i--){
			CarOrder c = prodSchedNormal.getNextCarOrder(60);
			assertEquals(2,c.getCarOrderID());
		}
		CarOrder c = prodSchedNormal.getNextCarOrder(60);
		assertEquals(3,c.getCarOrderID());
	}

}

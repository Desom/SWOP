package domain.order;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import domain.assembly.AssemblyLineScheduler;
import domain.assembly.Scheduler;
import domain.assembly.algorithm.FIFOSchedulingAlgorithm;
import domain.assembly.algorithm.SchedulingAlgorithm;
import domain.assembly.algorithm.SpecificationBatchSchedulingAlgorithm;
import domain.configuration.CarModel;
import domain.configuration.CarModelCatalog;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.CarOrder;
import domain.order.OrderManager;
import domain.policies.CompletionPolicy;
import domain.policies.ConflictPolicy;
import domain.policies.DependencyPolicy;
import domain.policies.InvalidConfigurationException;
import domain.policies.ModelCompatibilityPolicy;
import domain.policies.Policy;
import domain.user.GarageHolder;


public class OrderManagerTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public OrderManager orderManager;
	private static CarModelCatalog catalog;
	private static GarageHolder user1;
	private static GarageHolder user2;
	private static GarageHolder user3;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		catalog = new CarModelCatalog();
		user1 = new GarageHolder(1);
		user2 = new GarageHolder(2);
		user3 = new GarageHolder(3);
	}
	
	@Before
	public void setUp() throws Exception {
		ArrayList<SchedulingAlgorithm> possibleAlgorithms = new ArrayList<SchedulingAlgorithm>();
		possibleAlgorithms.add(new FIFOSchedulingAlgorithm());
		possibleAlgorithms.add(new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm()));
		GregorianCalendar time = new GregorianCalendar(2014, 1, 1, 12, 0, 0);
		CarModelCatalog catalog = new CarModelCatalog();
		Scheduler scheduler = new AssemblyLineScheduler(time, possibleAlgorithms);
		orderManager = new OrderManager(scheduler, "testData/testData_OrderManager.txt", catalog, time);
	}


	@Test
	public void testGetOrders(){
		ArrayList<Order> orders1 = orderManager.getOrders(user1);
		ArrayList<Order> orders2 = orderManager.getOrders(user2);
		ArrayList<Order> orders3 = orderManager.getOrders(user3);
		
		assertEquals(1,orders1.size());
		assertEquals(3,orders2.size());
		assertEquals(0,orders3.size());
		
		assertEquals(1,orders1.get(0).getCarOrderID());
		assertEquals(2,orders2.get(0).getCarOrderID());
		assertEquals(3,orders2.get(1).getCarOrderID());
		assertEquals(4,orders2.get(2).getCarOrderID());
	}

	
	@Test
	public void testPlaceOrder() throws InvalidConfigurationException{
		//TODO is er een betere manier dan telkens equals met een option description?
		CarModel model = null;
		for(CarModel m : catalog.getAllModels()){
			if(m.getName().equals("Model A"))
				model = m;
		}
		ArrayList<Option> options = new ArrayList<Option>();
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
				options.add(option);
		}
		ArrayList<OptionType> List = new ArrayList<OptionType>();
		for(OptionType i: OptionType.values()){
			if(i != OptionType.Airco || i != OptionType.Spoiler ){
				List.add(i);
			}
		}
		Policy pol1 = new CompletionPolicy(null,List);
		Policy pol2 = new ConflictPolicy(pol1);
		Policy pol3 = new DependencyPolicy(pol2);
		Policy pol4 = new ModelCompatibilityPolicy(pol3);
		Policy carOrderPolicy= pol4;
		
		Configuration config = new Configuration(model, pol4);
		for(Option option: options){			
			config.addOption(option);
		}
		orderManager.placeCarOrder(user3, config);
		ArrayList<Order> orders = orderManager.getOrders(user3);
		assertEquals(5,orders.get(0).getCarOrderID());
		assertEquals(3,orders.get(0).getUserId());
	}

	@Test
	public void testCompletionEstimate(){
		ArrayList<Order> orders1 = orderManager.getOrders(user1);
		GregorianCalendar cal = orderManager.completionEstimate(orders1.get(0));
		assertEquals(2013,orderManager.completionEstimate(orders1.get(0)).get(GregorianCalendar.YEAR));
		ArrayList<Order> orders2 = orderManager.getOrders(user2);

		Order order2_0 = orders2.get(0);
		assertTrue(order2_0.toString().startsWith("CarOrder: 2"));
		assertEquals(15,orderManager.completionEstimate(order2_0).get(GregorianCalendar.HOUR_OF_DAY));
		Order order2_1 = orders2.get(1);
		assertTrue(order2_1.toString().startsWith("CarOrder: 3"));
		assertEquals(16,orderManager.completionEstimate(order2_1).get(GregorianCalendar.HOUR_OF_DAY));
		Order order2_2 = orders2.get(2);
		assertTrue(order2_2.toString().startsWith("CarOrder: 4"));
		assertEquals(17,orderManager.completionEstimate(order2_2).get(GregorianCalendar.HOUR_OF_DAY));
	}
	
	
	@Test
	public void testGetPendingOrders(){
		ArrayList<Order> pendOrder1 = orderManager.getPendingOrders(user1);
		assertEquals(0,pendOrder1.size());

		ArrayList<Order> pendOrder2 = orderManager.getPendingOrders(user2);
		assertEquals(3,pendOrder2.size());
		assertEquals(2,pendOrder2.get(0).getCarOrderID());
		assertEquals(3,pendOrder2.get(1).getCarOrderID());
		assertEquals(4,pendOrder2.get(2).getCarOrderID());

		ArrayList<Order> pendOrder3 = orderManager.getPendingOrders(user3);
		assertEquals(0,pendOrder3.size());
	}

	
	@Test
	public void testGetCompletedOrders(){
		ArrayList<Order> compOrder1 = orderManager.getCompletedOrders(user1);
		assertEquals(1,compOrder1.size());
		
		assertEquals(1,compOrder1.get(0).getCarOrderID());
		
		ArrayList<Order> compOrder2 = orderManager.getCompletedOrders(user2);
		assertEquals(0,compOrder2.size());
		ArrayList<Order> compOrder3 = orderManager.getCompletedOrders(user3);
		assertEquals(0,compOrder3.size());
	}
	
}

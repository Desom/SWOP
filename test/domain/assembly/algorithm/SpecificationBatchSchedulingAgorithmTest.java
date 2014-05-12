package domain.assembly.algorithm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import domain.Statistics;
import domain.assembly.AssemblyLine;
import domain.assembly.AssemblyLineScheduler;
import domain.assembly.ScheduledOrder;
import domain.configuration.CarModelCatalog;
import domain.configuration.CarModelCatalogException;
import domain.configuration.Configuration;
import domain.configuration.Option;
import domain.configuration.OptionType;
import domain.order.CarOrder;
import domain.order.Order;
import domain.order.OrderManager;
import domain.policies.CompletionPolicy;
import domain.policies.InvalidConfigurationException;
import domain.user.GarageHolder;

public class SpecificationBatchSchedulingAgorithmTest {

	SpecificationBatchSchedulingAlgorithm algorithm;
	GarageHolder garageHolder;
	CarModelCatalog cmc;
	AssemblyLineScheduler als;
	AssemblyLine line;
	Configuration specified = null;
	
	@Before
	public void testCreate() throws IOException, CarModelCatalogException, InvalidConfigurationException {
		this.cmc = new CarModelCatalog();
		makeConfiguration();
		this.algorithm = new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm());
		this.algorithm.setConfiguration(specified);
		ArrayList<SchedulingAlgorithm> list = new ArrayList<SchedulingAlgorithm>();
		list.add(new FIFOSchedulingAlgorithm());
		list.add(algorithm);
		this.garageHolder = new GarageHolder(0);
		this.als = new AssemblyLineScheduler(new GregorianCalendar(2000,0,1,6,0,0), list);
		this.als.setSchedulingAlgorithm(this.algorithm);
		@SuppressWarnings("unused")
		AssemblyLine assembly = new AssemblyLine(als);

	}
	
	@Test
	public void testScheduleToList() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderList();
		
		ArrayList<Order> scheduleList = algorithm.scheduleToList(orderList, als);

		assertEquals(9,scheduleList.get(0).getCarOrderID());
		assertEquals(6,scheduleList.get(1).getCarOrderID());
		assertEquals(3,scheduleList.get(2).getCarOrderID());
		assertEquals(0,scheduleList.get(3).getCarOrderID());
		assertEquals(10,scheduleList.get(4).getCarOrderID());
		assertEquals(11,scheduleList.get(5).getCarOrderID());
		assertEquals(8,scheduleList.get(6).getCarOrderID());
		assertEquals(7,scheduleList.get(7).getCarOrderID());
		assertEquals(5,scheduleList.get(8).getCarOrderID());
		assertEquals(4,scheduleList.get(9).getCarOrderID());
		assertEquals(2,scheduleList.get(10).getCarOrderID());
		assertEquals(1,scheduleList.get(11).getCarOrderID());
	}

	@Test
	public void testScheduleToScheduledOrderList() throws InvalidConfigurationException{
		ArrayList<Order> orderList = makeOrderList();
		
		ArrayList<ScheduledOrder> scheduleList = algorithm.scheduleToScheduledOrderList(orderList,new GregorianCalendar(2000,0,1,12,0,0),this.als.getAssemblyLine().StateWhenAcceptingOrders(), als);
		GregorianCalendar time = new GregorianCalendar(2000,0,1,12,0,0);//12h
		assertEquals(9,scheduleList.get(0).getScheduledOrder().getCarOrderID());//60
		assertEquals(time,scheduleList.get(0).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//13h00
		assertEquals(6,scheduleList.get(1).getScheduledOrder().getCarOrderID());//60
		assertEquals(time,scheduleList.get(1).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//14h00
		assertEquals(3,scheduleList.get(2).getScheduledOrder().getCarOrderID());//60
		assertEquals(time,scheduleList.get(2).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//15h00
		assertEquals(0,scheduleList.get(3).getScheduledOrder().getCarOrderID());//60
		assertEquals(time,scheduleList.get(3).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 60);//16h00
		assertEquals(10,scheduleList.get(4).getScheduledOrder().getCarOrderID());//70
		assertEquals(time,scheduleList.get(4).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//17h10
		assertEquals(11,scheduleList.get(5).getScheduledOrder().getCarOrderID());//50
		assertEquals(time,scheduleList.get(5).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//18h20
		assertEquals(8,scheduleList.get(6).getScheduledOrder().getCarOrderID());//50
		assertEquals(time,scheduleList.get(6).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//19h30
		
		assertEquals(null,scheduleList.get(7).getScheduledOrder());//0 (70)
		assertEquals(time,scheduleList.get(7).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 50);//22h20
		assertEquals(null,scheduleList.get(8).getScheduledOrder());//0 (50)
		assertEquals(time,scheduleList.get(8).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 50);//21h50
		assertEquals(null,scheduleList.get(9).getScheduledOrder());//0 (50)
		assertEquals(time,scheduleList.get(9).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 0);
		
		//nieuwe dag
		time = new GregorianCalendar(2000,0,2,6,0,0);//06h00
		
		
		assertEquals(7,scheduleList.get(10).getScheduledOrder().getCarOrderID());//70
		assertEquals(time,scheduleList.get(10).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//7h10
		assertEquals(5,scheduleList.get(11).getScheduledOrder().getCarOrderID());//50
		assertEquals(time,scheduleList.get(11).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//8h20
		assertEquals(4,scheduleList.get(12).getScheduledOrder().getCarOrderID());//70
		assertEquals(time,scheduleList.get(12).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//9h30
		assertEquals(2,scheduleList.get(13).getScheduledOrder().getCarOrderID());//50
		assertEquals(time,scheduleList.get(13).getScheduledTime());
		time.add(GregorianCalendar.MINUTE, 70);//10h40
		assertEquals(1,scheduleList.get(14).getScheduledOrder().getCarOrderID());//70
		assertEquals(time,scheduleList.get(14).getScheduledTime());
	}
	
	/**
	 * @return
	 * @throws InvalidConfigurationException
	 */
	private ArrayList<Order> makeOrderList()
			throws InvalidConfigurationException {
		ArrayList<Order> orderList = new ArrayList<Order>();
		Configuration config1 = new Configuration(cmc.getAllModels().get(0), new CompletionPolicy(null,new ArrayList<OptionType>()));
		Configuration config2 = new Configuration(cmc.getAllModels().get(1), new CompletionPolicy(null,new ArrayList<OptionType>()));
		Configuration config3 = new Configuration(cmc.getAllModels().get(2), new CompletionPolicy(null,new ArrayList<OptionType>()));
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sedan")
					||option.getDescription().equals("blue")
					||option.getDescription().equals("standard 2l v4")
					||option.getDescription().equals("5 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("comfort")
					){
				config1.addOption(option); // Model C
				config2.addOption(option); // Model B = 70
				config3.addOption(option); // Model A = 50
			}
		}
		config1.complete();
		config2.complete();
		config3.complete();
		GregorianCalendar time = new GregorianCalendar(2000,0,1,12,0,0);
		
		orderList.add(new CarOrder(0, garageHolder, config1, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(1, garageHolder, config2, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(2, garageHolder, config3, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(3, garageHolder, config1, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(4, garageHolder, config2, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(5, garageHolder, config3, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(6, garageHolder, config1, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(7, garageHolder, config2, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(8, garageHolder, config3, time));
		time.add(GregorianCalendar.MINUTE, -1);
		orderList.add(new CarOrder(9, garageHolder, config1, time));
		time.add(GregorianCalendar.HOUR, -1);
		orderList.add(new CarOrder(10, garageHolder, config2, time));
		time.add(GregorianCalendar.MINUTE, 1);
		orderList.add(new CarOrder(11, garageHolder, config3, time));
		
		return orderList;
	}
	
	@Test
	public void testSearchConfiguration() throws IOException, CarModelCatalogException, InvalidConfigurationException{
		ArrayList<SchedulingAlgorithm> possibleAlgorithms = new ArrayList<SchedulingAlgorithm>();
		SpecificationBatchSchedulingAlgorithm algo = new SpecificationBatchSchedulingAlgorithm(new FIFOSchedulingAlgorithm());
		possibleAlgorithms.add(new FIFOSchedulingAlgorithm());
		possibleAlgorithms.add(algo);
		GregorianCalendar time = new GregorianCalendar(2014, 9, 1, 6, 0, 0);
		CarModelCatalog catalog = new CarModelCatalog();
		AssemblyLineScheduler scheduler = new AssemblyLineScheduler(time, possibleAlgorithms);
		scheduler.setSchedulingAlgorithm(algo);
		OrderManager orderManager = new OrderManager(scheduler, "testData/testData_OrderManager.txt", catalog, time);
		Statistics stat = new Statistics(orderManager);
		@SuppressWarnings("unused")
		AssemblyLine line = new AssemblyLine(scheduler);
		
		assertEquals(algo.searchForBatchConfiguration(scheduler).size(), 1);
	}
	
	public void makeConfiguration() throws InvalidConfigurationException{
		Configuration config1 = new Configuration(cmc.getAllModels().get(0), new CompletionPolicy(null,new ArrayList<OptionType>()));
		for(Option option : cmc.getAllOptions()){
			if(option.getDescription().equals("sedan")
					||option.getDescription().equals("blue")
					||option.getDescription().equals("standard 2l v4")
					||option.getDescription().equals("5 speed manual")
					||option.getDescription().equals("leather white")
					||option.getDescription().equals("comfort")
					){
				config1.addOption(option); // Model C
			}
		}
		this.specified = config1;
	}

}
